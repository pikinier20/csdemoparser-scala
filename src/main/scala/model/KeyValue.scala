package demoparser
package model

import interfaces.ValueInterface

import demoparser.serialization.JsonDemoSerializer

sealed trait Value[T] extends ValueInterface[T] {
  override def prettyPrint: String = v.toString
  def v: T

  override def toJson: String = {
    import io.circe.generic.auto._
    import JsonDemoSerializer.valueSerializer
    JsonDemoSerializer.serializeIfPossible(this)
  }
}

case class StringValue(v: String) extends Value[String]
case class FloatValue(v: Float) extends Value[Float]
case class LongValue(v: Long) extends Value[Long]
case class IntValue(v: Int) extends Value[Int]
case class BooleanValue(v: Boolean) extends Value[Boolean]
