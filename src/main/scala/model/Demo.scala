package demoparser
package model

import interfaces.{DemoInterface, GameEventInterface}

import demoparser.serialization.JsonDemoSerializer

import scala.collection.JavaConverters._

case class Demo(header: Header, events: Seq[GameEvent]) extends DemoInterface {
  override def prettyPrint: String =
    header.prettyPrint + "\n" + events.map(_.prettyPrint).mkString("\n")
  override def eventsList(): java.util.List[GameEventInterface] = {
    val e: Seq[GameEventInterface] = events
    e.asJava
  }

  override def toJson: String = {
    import io.circe.generic.auto._
    JsonDemoSerializer.serializeIfPossible(this)
  }
}
