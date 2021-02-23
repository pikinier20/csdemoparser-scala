package demoparser

import parser.DemoParser

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
    case Right(v)    => println(v.prettyPrint)
    case Left(value) => println(value)
  }
  Await.ready(print, Duration.Inf)
}
