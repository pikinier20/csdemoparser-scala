package demoparser
package parser.model

import model.{GameEvent, PrettyPrintable}

case class Tick(tick: Int, events: Seq[GameEvent]) extends PrettyPrintable {
  override def prettyPrint: String =
    s"Tick: $tick\n" + events.map("\t" + _).mkString("\n")
}
