package demoparser

import parser.DemoParser

import java.nio.file.Paths

object Main extends App {
  val parser = new DemoParser()
  parser.parseFromPath(
    Paths.get("/home/fzybala/Pobrane/navi-junior-vs-gambit-vertigo.dem")
  )
}
