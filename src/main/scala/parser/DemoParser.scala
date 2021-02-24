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

  def parse()(implicit ec: ExecutionContext): Future[Either[String, Demo]] =
    for {
      header <- Future.successful(parseHeader)
      events <- parseContent()
      gameEvents = events.flatMap(gameEventProcessor.process)
      demo = for {
        h <- header
        ge <- gameEvents
      } yield Demo(h, ge)
    } yield demo

  @tailrec
  private def parseContent(
      acc: Future[Seq[PBGameEvent]] = Future.successful(Seq())
  )(implicit ec: ExecutionContext): Future[Either[String, Seq[PBGameEvent]]] = {
    def ignore(): Future[Either[String, Seq[Nothing]]] = {
      buffer.readIBytes
      Future.successful(Right(Seq()))
    }
    def empty(): Future[Either[String, Seq[Nothing]]] =
      Future.successful(Right(Seq()))
    val cNo = buffer.readUInt8
    val tick = buffer.readInt
    val playerSlot = buffer.readUInt8
    PBCommand.commandOption(cNo) match {
      case Some(command) =>
        if (command == PBCommand.Stop) {
          acc.map(Right(_))
        } else {
          val eventPortion = command match {
            case PBCommand.Packet | PBCommand.Signon =>
              DemoPacketHandler.handle(tick, buffer)
            case PBCommand.DataTables   => ignore()
            case PBCommand.StringTables => ignore()
            case PBCommand.ConsoleCmd   => ignore()
            case PBCommand.UserCmd      => buffer.readInt; ignore();
            case PBCommand.Stop         => empty()
            case PBCommand.CustomData   => empty()
            case PBCommand.SyncTick     => empty()
          }
          val temp = eventPortion.map(
            _.fold(
              s => {
                println(s"Error on handler for command $command: $s")
                Seq()
              },
              s => s
            )
          )
          parseContent(
            acc.flatMap(ac => temp.map(_ ++ ac))
          )
        }

      case None => Future.successful(Left(s"Undefined command: $cNo"))
    }

  }

  private def parseHeader: Either[String, Header] = {
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
    if (magic != "HL2DEMO")
      Left("Processed file is not correct CS:GO demo. Wrong magic number")
    else
      Right(
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
