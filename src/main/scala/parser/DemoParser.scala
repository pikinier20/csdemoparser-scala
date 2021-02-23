package demoparser
package parser

import demoparser.io.DemoBuffer
import demoparser.model.{Demo, Header, Message, NETMessage, SVCMessage}
import demoparser.parser.handlers.DemoPacketHandler
import demoparser.parser.model.{PBCommand, PBGameEvent, Tick}
import demoparser.parser.processors.DefaultGameEventsProcessor
import netmessages.{CNETMsg_SplitScreenUser, NET_Messages, SVC_Messages}
import netmessages.NET_Messages.Unrecognized

import java.nio.ByteBuffer
import java.nio.file.Path
import scala.Seq
import scala.annotation.tailrec
import scala.concurrent.{ExecutionContext, Future}

class DemoParser(val buffer: DemoBuffer) {
  val gameEventProcessor = DefaultGameEventsProcessor

  def parse()(implicit ec: ExecutionContext): Future[Either[String, Demo]] = {
    val header = parseHeader
    val events = parseContent()
    val gameEvents = events.map(gameEventProcessor.process)
    gameEvents.map(_.map(Demo(header, _)))
  }
  @tailrec
  private def parseContent(
      acc: Seq[Future[Seq[PBGameEvent]]] = Seq()
  )(implicit ec: ExecutionContext): Future[Seq[PBGameEvent]] = {
    def ignore(): Future[Seq[Nothing]] = {
      buffer.readIBytes
      Future { Seq() }
    }
    val command = PBCommand(buffer.readUInt8)
    println(command)
    val tick = buffer.readInt
    val eventPortion = command match {
      case PBCommand.Packet | PBCommand.Signon =>
        DemoPacketHandler.handle(tick, buffer)
      case PBCommand.DataTables   => ignore()
      case PBCommand.StringTables => ignore()
      case PBCommand.ConsoleCmd   => ignore()
      case PBCommand.UserCmd      => buffer.readInt; ignore();
      case PBCommand.Stop         => Future(Seq())
      case PBCommand.CustomData   => Future(Seq())
      case PBCommand.SyncTick     => Future(Seq())
    }
    val res = eventPortion
    if (command == PBCommand.Stop) Future.sequence(acc).map(_.flatten)
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
