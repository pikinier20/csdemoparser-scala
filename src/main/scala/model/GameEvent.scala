package demoparser
package model

case class GameEvent(name: String, tick: Int, keys: Map[String, Value[_]])
    extends PrettyPrintable {
  override def prettyPrint: String = {
    s"""{
      |name: $name
      |tick: $tick
      |${keys.map { case (k, v) => s"$k: $v" }.mkString("\n")}
    }""".stripMargin
  }
}
