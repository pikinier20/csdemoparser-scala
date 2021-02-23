package demoparser
package parser.model

import model.PrettyPrintable
import scalapb.GeneratedMessage

case class PBGameEvent(tick: Int, content: GeneratedMessage)
