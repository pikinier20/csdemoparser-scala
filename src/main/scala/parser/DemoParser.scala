package demoparser
package parser

import demoparser.buffer.DemoBuffer
import demoparser.config.ParserConfig
import demoparser.model.{Demo, Header}
import demoparser.parser.handlers.DemoPacketHandler
import demoparser.parser.model.{PBCommand, PBGameEvent}
import demoparser.parser.processors.{
  DefaultGameEventsProcessor,
  GameEventsProcessor
}
import netmessages.{CNETMsg_SplitScreenUser, NET_Messages, SVC_Messages}
import netmessages.NET_Messages.Unrecognized

import java.nio.ByteBuffer
import java.nio.file.Path
import scala.Seq
import scala.annotation.tailrec
import scala.concurrent.{ExecutionContext, Future}

class DemoParser(val buffer: DemoBuffer, implicit val config: ParserConfig) {
  val gameEventProcessor: GameEventsProcessor = DefaultGameEventsProcessor

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
    val tick = buffer.readInt
    val playerSlot = buffer.readUInt8
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
      path: Path,
      config: ParserConfig
  )(implicit ec: ExecutionContext): Future[Either[String, Demo]] = {
    val buffer = DemoBuffer(path)
    new DemoParser(buffer, config).parse()
  }
}
