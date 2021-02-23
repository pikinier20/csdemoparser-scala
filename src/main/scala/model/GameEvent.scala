package demoparser
package model

case class GameEvent(name: String, tick: Int, keys: Map[String, Any])
    extends PrettyPrintable {
  override def prettyPrint: String = {
    s"""{\n
          |name: $name
          |tick: $tick
          |${keys.map { case (k, v) => s"$k: $v" }.mkString("\n")}
        }""".stripMargin
  }
}
