package demoparser
package model

trait Event extends PrettyPrintable

case class MessageEvent(name: String, content: String) extends Event {
  override def prettyPrint: String = s"$name: $content"
}
