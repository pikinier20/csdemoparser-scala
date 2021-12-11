package demoparser
package parser

import com.google.protobuf.InvalidProtocolBufferException
import demoparser.buffer.{DefaultDemoBuffer, DemoBuffer}
import demoparser.config.{ParserConfig, ParserConfigInterface}
import demoparser.interfaces.{
  DemoInterface,
  DemoParserInterface,
  DemoParsingException
}
import demoparser.model.{Demo, Header}
import demoparser.parser.handlers.DemoPacketHandler
import demoparser.parser.model.{PBCommand, PBGameEvent}
import demoparser.parser.processors.{
  DefaultGameEventsProcessor,
  GameEventsProcessor
}
import netmessages.{CNETMsg_SplitScreenUser, NET_Messages, SVC_Messages}
import netmessages.NET_Messages.Unrecognized

import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.file.Path
import java.util
import java.util.concurrent.ExecutorService
import scala.Seq
import scala.annotation.tailrec
import scala.concurrent.impl.Promise
import scala.concurrent.{
  ExecutionContext,
  ExecutionContextExecutorService,
  Future
}
import scala.util.{Failure, Success, Try}

class DemoParser(
    val buffer: DemoBuffer,
    implicit val config: ParserConfigInterface
) {
  val gameEventProcessor: GameEventsProcessor = DefaultGameEventsProcessor

  def parse()(implicit ec: ExecutionContext): Future[Either[String, Demo]] =
    for {
      header <- Future.successful(parseHeader)
      events <-
        if (header.isRight) parseContent() else Future.successful(Right(Seq()))
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
          val eventPortion =
            Try(command match {
              case PBCommand.Packet | PBCommand.Signon =>
                DemoPacketHandler.handle(tick, buffer)
              case PBCommand.DataTables   => ignore()
              case PBCommand.StringTables => ignore()
              case PBCommand.ConsoleCmd   => ignore()
              case PBCommand.UserCmd      => buffer.readInt; ignore();
              case PBCommand.Stop         => empty()
              case PBCommand.CustomData   => empty()
              case PBCommand.SyncTick     => empty()
            })
          eventPortion match {
            case Failure(exception: InvalidProtocolBufferException) =>
              println(
                s"Got exception on parsing next chunk: $exception. Returning current result"
              )
              acc.map(Right(_))
            case Success(value) =>
              val temp = value.map(
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

object DemoParser extends DemoParserInterface {
  def parseFromPath(
      path: Path,
      config: ParserConfigInterface
  )(implicit ec: ExecutionContext): Future[Either[String, Demo]] = {
    val buffer = DefaultDemoBuffer(path)
    new DemoParser(buffer, config).parse()
  }

  def parseFromInputStream(
      inputStream: InputStream,
      config: ParserConfigInterface
  )(implicit ec: ExecutionContext): Future[Either[String, Demo]] = {
    val buffer = DefaultDemoBuffer(inputStream)
    new DemoParser(buffer, config).parse()
  }

  override def parseFromPath(
      path: Path,
      config: ParserConfigInterface,
      exService: ExecutorService
  ): util.concurrent.Future[DemoInterface] = {
    import scala.jdk.FutureConverters._
    implicit val ctx: ExecutionContext =
      ExecutionContext.fromExecutorService(exService)
    parseFromPath(path, config)
      .map(e =>
        e.fold[DemoInterface](
          s => throw new DemoParsingException(s),
          d => d
        )
      )
      .asJava
      .toCompletableFuture
  }

  override def parseFromInputStream(
      input: InputStream,
      config: ParserConfigInterface,
      exService: ExecutorService
  ): util.concurrent.Future[DemoInterface] = {
    import scala.jdk.FutureConverters._
    implicit val ctx: ExecutionContext =
      ExecutionContext.fromExecutorService(exService)
    parseFromInputStream(input, config)
      .map(e =>
        e.fold[DemoInterface](
          s => throw new DemoParsingException(s),
          d => d
        )
      )
      .asJava
      .toCompletableFuture
  }
}
