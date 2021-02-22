package demoparser

import parser.DemoParser

import demoparser.model.MessageEvent

import java.nio.file.Paths
import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor}

object Main extends App {
  val demo = DemoParser.parseFromPath(
    Paths.get("/home/fzybala/Pobrane/navi-junior-vs-gambit-vertigo.dem")
  )
  val print = demo.map {
    case Right(v) =>
      v.ticks
        .map(t =>
          t.copy(events = t.events.filter {
            case m: MessageEvent =>
              m.name == "svc_GameEvent" || m.name == "svc_GameEventList"
            case _ => false
          })
        )
        .foreach(t => println(t.prettyPrint))
    case Left(value) => println(value)
  }
  Await.ready(print, Duration.Inf)
}
