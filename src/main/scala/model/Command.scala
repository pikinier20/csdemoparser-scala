package demoparser
package model

object Command extends Enumeration {
  import Command._
  val Signon: Value = Value(1)
  val Packet: Value = Value(2)
  val SyncTick: Value = Value(3)
  val ConsoleCmd: Value = Value(4)
  val UserCmd: Value = Value(5)
  val DataTables: Value = Value(6)
  val Stop: Value = Value(7)
  val CustomData: Value = Value(8)
  val StringTables: Value = Value(9)
}
