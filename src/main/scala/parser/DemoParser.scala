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
import scala.concurrent.{ExecutionContext, Future}

class DemoParser(val buffer: DemoBuffer) {

  def parse()(implicit ec: ExecutionContext): Future[Either[String, Demo]] = {
    val header = parseHeader
    val ticks = parseContent()
    val ticksF = Future.sequence(ticks)
    ticksF.map(ticks => Right(Demo(header, ticks)))
  }
  @tailrec
  private def parseContent(
      acc: Seq[Future[Tick]] = Seq()
  )(implicit ec: ExecutionContext): Seq[Future[Tick]] = {
    def ignore(): Future[Seq[Nothing]] = {
      buffer.readIBytes
      Future { Seq() }
    }
    val command = Command(buffer.readUInt8)
    val tick = buffer.readInt
    val playerSlot = buffer.readUInt8
    val eventPortion = command match {
      case Command.Packet | Command.Signon => DemoPacketHandler.handle(buffer)
      case Command.DataTables              => ignore()
      case Command.StringTables            => ignore()
      case Command.ConsoleCmd              => ignore()
      case Command.UserCmd                 => buffer.readInt; ignore();
      case Command.Stop                    => Future(Seq())
      case Command.CustomData              => Future(Seq())
      case Command.SyncTick                => Future(Seq())
    }
    val res = eventPortion.map(Tick(tick, _))
    if (command == Command.Stop) acc
    else
      parseContent(acc :+ res)
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
  def parseFromPath(
      path: Path
  )(implicit ec: ExecutionContext): Future[Either[String, Demo]] = {
    val buffer = DemoBuffer(path)
    new DemoParser(buffer).parse()
  }
}
