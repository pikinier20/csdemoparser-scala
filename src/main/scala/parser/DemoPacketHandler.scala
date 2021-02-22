package demoparser
package parser

import io.DemoBuffer

import demoparser.model.{Event, Message, MessageEvent, NETMessage, SVCMessage}
import netmessages.{NET_Messages, SVC_Messages}

import scala.annotation.tailrec
import scala.concurrent.{ExecutionContext, Future}

object DemoPacketHandler {
  def handle(
      buffer: DemoBuffer
  )(implicit ec: ExecutionContext): Future[Seq[Event]] = {
    buffer.skip(152)
    buffer.readInt
    buffer.readInt
    val chunk = buffer.readIBytes
    Future { parseChunk(chunk) }
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
  private def getMessageHandler(message: Message[_]): Array[Byte] => String =
    MessageHandler(message)
  @tailrec
  private def parseChunk(
      buffer: DemoBuffer,
      acc: Seq[Event] = Seq()
  ): Seq[Event] = {
    if (!buffer.hasRemaining) acc
    else {
      val command = buffer.readVarint32
      val size = buffer.readVarint32
      val msg = findMessage(command) match {
        case Right(msg) =>
          Seq(
            MessageEvent(
              msg.name,
              getMessageHandler(msg)(buffer.readBytes(size))
            )
          )
        case Left(error) =>
          println(error)
          Seq()
      }
      parseChunk(buffer, acc ++ msg)
    }
  }
}
