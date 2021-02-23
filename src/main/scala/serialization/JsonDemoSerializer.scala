package demoparser
package serialization
import model.{
  BooleanValue,
  Demo,
  FloatValue,
  GameEvent,
  IntValue,
  LongValue,
  StringValue,
  Value
}

import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

object JsonDemoSerializer extends DemoSerializer {
  implicit val valueSerializer: Encoder[Value[_]] = {
    case StringValue(v)  => v.asJson
    case FloatValue(v)   => v.asJson
    case LongValue(v)    => v.asJson
    case BooleanValue(v) => v.asJson
    case IntValue(v)     => v.asJson
  }
  override def serialize(demo: Demo): String = demo.asJson.toString()

}
