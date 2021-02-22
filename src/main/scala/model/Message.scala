package demoparser
package model

import netmessages.{NET_Messages, SVC_Messages}

sealed trait Message[T] {
  def name: String
  def msg: T
}

case class SVCMessage(msg: SVC_Messages) extends Message[SVC_Messages] {
  def name = msg.name
}

case class NETMessage(msg: NET_Messages) extends Message[NET_Messages] {
  def name = msg.name
}
