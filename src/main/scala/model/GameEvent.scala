package demoparser
package model

import interfaces.{GameEventInterface, ValueInterface}

import java.util
import scala.collection.JavaConverters._

case class GameEvent(name: String, tick: Int, keys: Map[String, Value[_]])
    extends PrettyPrintable
    with GameEventInterface {
  override def prettyPrint: String = {
    s"""
      |GameEvent: {
      |  name: $name
      |  tick: $tick
      |  ${keys.map { case (k, v) => s"$k: $v" }.mkString("\n")}
      |}
    |""".stripMargin
  }

  override def keysMap(): util.Map[String, ValueInterface[_]] = {
    val map: Map[String, ValueInterface[_]] = keys
    map.asJava
  }
}
