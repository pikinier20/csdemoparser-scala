package demoparser
package serialization
import model._

import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

object JsonDemoSerializer {
  implicit def valueSerializer: Encoder[Value[_]] = {
    case StringValue(v)  => v.asJson
    case FloatValue(v)   => v.asJson
    case LongValue(v)    => v.asJson
    case BooleanValue(v) => v.asJson
    case IntValue(v)     => v.asJson
  }

  def serialize(demo: Demo): String = demo.asJson.toString()

  def serialize(header: Header): String = header.asJson.toString()

  def serialize(gameEvent: GameEvent): String = gameEvent.asJson.toString()

  def serialize(value: Value[_]): String = valueSerializer(value).toString()

}
