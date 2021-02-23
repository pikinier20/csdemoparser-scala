package demoparser
package model

sealed trait Value[T] extends PrettyPrintable {
  override def prettyPrint: String = v.toString
  def v: T
}

case class StringValue(v: String) extends Value[String]
case class FloatValue(v: Float) extends Value[Float]
case class LongValue(v: Long) extends Value[Long]
case class IntValue(v: Int) extends Value[Int]
case class BooleanValue(v: Boolean) extends Value[Boolean]
