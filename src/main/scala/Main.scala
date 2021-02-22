package demoparser

import parser.DemoParser

import java.nio.file.Paths

object Main extends App {
  DemoParser.parseFromPath(
    Paths.get("/home/fzybala/Pobrane/navi-junior-vs-gambit-vertigo.dem")
  )
}
