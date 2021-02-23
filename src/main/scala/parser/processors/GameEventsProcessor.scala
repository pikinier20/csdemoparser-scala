package demoparser
package parser.processors

import parser.model.PBGameEvent

import demoparser.model.GameEvent
import netmessages.CSVCMsg_GameEvent.key_t
import netmessages.{CSVCMsg_GameEvent, CSVCMsg_GameEventList}

trait GameEventsProcessor {
  def process(pbEvents: Seq[PBGameEvent]): Either[String, Seq[GameEvent]]
}

object DefaultGameEventsProcessor extends GameEventsProcessor {
  override def process(
      pbEvents: Seq[PBGameEvent]
  ): Either[String, Seq[GameEvent]] = {
    pbEvents
      .map(_.content)
      .find(_.isInstanceOf[CSVCMsg_GameEventList])
      .fold[Either[String, Seq[GameEvent]]](Left("Cannot find GameEventList")) {
        case eventList: CSVCMsg_GameEventList =>
          processWithEventList(pbEvents, eventList)
      }
  }

  private class GameEventConstructor(desc: CSVCMsg_GameEventList.descriptor_t) {
    val name: String = desc.getName
    val id: Int = desc.getEventid
    val keyNames: Seq[String] = desc.keys.map(_.getName)

    private object EventKeyType extends Enumeration {
      val String: Value = Value(1)
      val Float: Value = Value(2)
      val Long: Value = Value(3)
      val Short: Value = Value(4)
      val Byte: Value = Value(5)
      val Bool: Value = Value(6)
      val UInt64: Value = Value(7)
      val WString: Value = Value(8)
    }

    private def convertValue(value: CSVCMsg_GameEvent.key_t) =
      EventKeyType(value.getType) match {
        case EventKeyType.String  => value.getValString
        case EventKeyType.Float   => value.getValFloat
        case EventKeyType.Long    => value.getValLong
        case EventKeyType.Short   => value.getValShort
        case EventKeyType.Byte    => value.getValByte
        case EventKeyType.Bool    => value.getValBool
        case EventKeyType.UInt64  => value.getValUint64
        case EventKeyType.WString => value.getValWstring
      }

    def apply(tick: Int, eventMsg: CSVCMsg_GameEvent): GameEvent = {
      val keys = keyNames.zip(eventMsg.keys).map {
        case (name, value) => name -> convertValue(value)
      }
      GameEvent(name, tick, keys.toMap)
    }
  }

  private def processWithEventList(
      pbEvents: Seq[PBGameEvent],
      list: CSVCMsg_GameEventList
  ): Either[String, Seq[GameEvent]] = {
    val gameEventConstructors = list.descriptors.map { desc =>
      desc.eventid -> new GameEventConstructor(desc)
    }.toMap
    Right(pbEvents.collect {
      case PBGameEvent(tick, content: CSVCMsg_GameEvent) =>
        gameEventConstructors(content.eventid)(tick, content)
    })
  }
}
