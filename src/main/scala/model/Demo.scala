package demoparser
package model

import interfaces.{DemoInterface, GameEventInterface}
import scala.collection.JavaConverters._

case class Demo(header: Header, events: Seq[GameEvent])
    extends PrettyPrintable
    with DemoInterface {
  override def prettyPrint: String =
    header.prettyPrint + "\n" + events.map(_.prettyPrint).mkString("\n")
  override def eventsList(): java.util.List[GameEventInterface] = {
    val e: Seq[GameEventInterface] = events
    e.asJava
  }
}
