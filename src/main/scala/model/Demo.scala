package demoparser
package model

case class Demo(header: Header, events: Seq[GameEvent])
    extends PrettyPrintable {
  override def prettyPrint: String =
    header.prettyPrint + "\n" + events.map(_.prettyPrint).mkString("\n")
}
