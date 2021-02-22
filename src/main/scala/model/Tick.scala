package demoparser
package model

case class Tick(tick: Int, events: Seq[Event]) extends PrettyPrintable {
  override def prettyPrint: String =
    s"Tick: $tick\n" + events.map("\t" + _).mkString("\n")
}
