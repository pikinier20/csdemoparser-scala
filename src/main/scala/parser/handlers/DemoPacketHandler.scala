package demoparser
package parser.handlers

import buffer.DemoBuffer
import parser.model.{Message, NETMessage, PBGameEvent, SVCMessage}
import scalapb.GeneratedMessage

import netmessages.{NET_Messages, SVC_Messages}

import scala.annotation.tailrec
import scala.concurrent.{ExecutionContext, Future}

object DemoPacketHandler {
  def handle(
      tick: Int,
      buffer: DemoBuffer
  )(implicit ec: ExecutionContext): Future[Either[String, Seq[PBGameEvent]]] = {
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
  ): (Array[Byte]) => Either[String, GeneratedMessage] =
    MessageHandler(message)
  @tailrec
  private def parseChunk(
      tick: Int,
      buffer: DemoBuffer,
      acc: Seq[PBGameEvent] = Seq()
  ): Either[String, Seq[PBGameEvent]] = {
    if (!buffer.hasRemaining) Right(acc)
    else {
      val command = buffer.readVarint32
      val size = buffer.readVarint32
      val msg = findMessage(command)
        .flatMap { m =>
          getMessageHandler(m)(buffer.readBytes(size)).map { msg =>
            Seq(
              PBGameEvent(
                tick,
                msg
              )
            )
          }
        }
      msg match {
        case Left(value)  => Left(value)
        case Right(value) => parseChunk(tick, buffer, acc ++ value)
      }
    }
  }
}
