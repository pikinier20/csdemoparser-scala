package demoparser
package parser

import demoparser.io.DemoBuffer
import demoparser.model.{
  Command,
  Demo,
  Event,
  Header,
  Message,
  MessageEvent,
  NETMessage,
  SVCMessage,
  Tick
}
import netmessages.{CNETMsg_SplitScreenUser, NET_Messages, SVC_Messages}
import netmessages.NET_Messages.Unrecognized

import java.nio.ByteBuffer
import java.nio.file.Path
import scala.Seq
import scala.annotation.tailrec

class DemoParser(val buffer: DemoBuffer) {

  def parse(): Either[String, Demo] = {
    val header = parseHeader
    println(header)
    val ticks = parseContent()
    ticks.foreach(t => println(t.prettyPrint))
    ???
  }
  @tailrec
  private def parseContent(acc: Seq[Tick] = Seq()): Seq[Tick] = {
    def ignore() = {
      buffer.readIBytes
      Seq()
    }
    val command = Command(buffer.readUInt8)
    val tick = buffer.readInt
    val playerSlot = buffer.readUInt8
    val eventPortion = command match {
      case Command.Packet | Command.Signon => handleDemoPacket
      case Command.DataTables              => ignore()
      case Command.StringTables            => ignore()
      case Command.ConsoleCmd              => ignore()
      case Command.UserCmd                 => { buffer.readInt; ignore(); }
      case Command.Stop                    => Seq()
      case Command.CustomData              => Seq()
      case Command.SyncTick                => Seq()
    }
    val res = Tick(tick, eventPortion)
    if (command == Command.Stop) acc
    else
      parseContent(acc :+ res)
  }

  private def handleDemoPacket: Seq[Event] = {
    def findMessage(messageValue: Int): Either[String, Message[_]] =
      NET_Messages.fromValue(messageValue) match {
        case NET_Messages.Unrecognized(unrecognizedValue) =>
          SVC_Messages.fromValue(unrecognizedValue) match {
            case SVC_Messages.Unrecognized(unrecognizedValue2) =>
              Left(s"Found unrecognized message with value $unrecognizedValue2")
            case other => Right(SVCMessage(other))
          }
        case other => Right(NETMessage(other))
      }
    def getMessageHandler(message: Message[_]): Array[Byte] => String =
      MessageHandler(message)
    @tailrec
    def parseChunk(buffer: DemoBuffer, acc: Seq[Event] = Seq()): Seq[Event] = {
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
            ).filter(m =>
              m.name == "svc_GameEvent" || m.name == "svc_GameEventList"
            )
          case Left(error) =>
            println(error)
            Seq()
        }
        parseChunk(buffer, acc ++ msg)
      }
    }

    buffer.skip(152)
    buffer.readInt
    buffer.readInt
    val chunk = buffer.readIBytes
    parseChunk(chunk)
  }

  private def parseHeader: Header = {
    val magic = buffer.readString(8)
    val protocol = buffer.readInt
    val networkProtocol = buffer.readInt
    val serverName = buffer.readMaxString
    val clientName = buffer.readMaxString
    val mapName = buffer.readMaxString
    val gameDirectory = buffer.readMaxString
    val playbackTime = buffer.readFloat
    val playbackTicks = buffer.readInt
    val playbackFrames = buffer.readInt
    val signonLength = buffer.readInt
    Header(
      magic,
      protocol,
      networkProtocol,
      serverName,
      clientName,
      mapName,
      gameDirectory,
      playbackTime,
      playbackTicks,
      playbackFrames,
      signonLength
    )
  }
}

object DemoParser {
  def parseFromPath(path: Path): Either[String, Demo] = {
    val buffer = DemoBuffer(path)
    new DemoParser(buffer).parse()
  }
}
