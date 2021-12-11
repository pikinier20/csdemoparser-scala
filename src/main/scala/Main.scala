package demoparser

import parser.DemoParser

import demoparser.config.ParserConfig
import demoparser.serialization.JsonDemoSerializer

import java.io.{File, FileOutputStream, PrintStream}
import java.nio.file.{Files, Paths}
import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor}

object Main extends App {
  val config = ParserConfig.Default
  val demo = DemoParser.parseFromPath(
    Paths.get("/home/fzybala/Pobrane/navi-junior-vs-gambit-vertigo.dem"),
    config
  )
  val fileOS = Files.newOutputStream(Files.createFile(Paths.get("./demo.json")))
  val ps = new PrintStream(fileOS)
  val json =
    demo.map(
      _.fold(s => println(s), d => ps.print(JsonDemoSerializer.serialize(d)))
    )
  val eventStats = demo.map(
    _.map(
      _.events.groupMapReduce(_.name)(_ => 1)((acc, next) => acc + next)
    ).foreach(_.foreach {
      case (k, v) => println(s"$k: $v")
    })
  )
  Await.ready(json, Duration.Inf)
  Await.ready(eventStats, Duration.Inf)
}
