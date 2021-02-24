package demoparser
package parser.model

object PBCommand extends Enumeration {
  val Signon: Value = Value(1)
  val Packet: Value = Value(2)
  val SyncTick: Value = Value(3)
  val ConsoleCmd: Value = Value(4)
  val UserCmd: Value = Value(5)
  val DataTables: Value = Value(6)
  val Stop: Value = Value(7)
  val CustomData: Value = Value(8)
  val StringTables: Value = Value(9)

  def commandOption(int: Int): Option[PBCommand.Value] =
    if (int >= 1 && int <= 9) Some(PBCommand(int)) else None
}
