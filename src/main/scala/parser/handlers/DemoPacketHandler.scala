package demoparser
package parser.handlers

import io.DemoBuffer
import model.{Message, NETMessage, SVCMessage}
import parser.model.PBGameEvent
import scalapb.GeneratedMessage

import netmessages.{NET_Messages, SVC_Messages}

import scala.annotation.tailrec
import scala.concurrent.{ExecutionContext, Future}

object DemoPacketHandler {
  def handle(
      tick: Int,
      buffer: DemoBuffer
  )(implicit ec: ExecutionContext): Future[Seq[PBGameEvent]] = {
    buffer.skip(152)
    buffer.readInt
    buffer.readInt
    val chunk = buffer.readIBytes
    Future { parseChunk(tick, chunk) }
  }

  private def findMessage(messageValue: Int): Either[String, Message[_]] =
    NET_Messages.fromValue(messageValue) match {
      case NET_Messages.Unrecognized(unrecognizedValue) =>
        SVC_Messages.fromValue(unrecognizedValue) match {
          case SVC_Messages.Unrecognized(unrecognizedValue2) =>
            Left(s"Found unrecognized message with value $unrecognizedValue2")
          case other => Right(SVCMessage(other))
        }
      case other => Right(NETMessage(other))
    }
  private def getMessageHandler(
      message: Message[_]
  ): Array[Byte] => GeneratedMessage =
    MessageHandler(message)
  @tailrec
  private def parseChunk(
      tick: Int,
      buffer: DemoBuffer,
      acc: Seq[PBGameEvent] = Seq()
  ): Seq[PBGameEvent] = {
    if (!buffer.hasRemaining) acc
    else {
      val command = buffer.readVarint32
      val size = buffer.readVarint32
      val msg = findMessage(command) match {
        case Right(msg) =>
          Seq(
            PBGameEvent(
              tick,
              getMessageHandler(msg)(buffer.readBytes(size))
            )
          )
        case Left(error) =>
          println(error)
          buffer.readBytes(size)
          Seq()
      }
      parseChunk(tick, buffer, acc ++ msg)
    }
  }
}
