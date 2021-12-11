package demoparser
package model

import interfaces.HeaderInterface

case class Header(
    magic: String,
    protocol: Int,
    networkProtocol: Int,
    serverName: String,
    clientName: String,
    mapName: String,
    gameDirectory: String,
    playbackTime: Float,
    playbackTicks: Int,
    playbackFrames: Int,
    signonLength: Int
) extends HeaderInterface {
  override def prettyPrint: String =
    s"""
      |Header: {
      |  magic: $magic
      |  protocol: $protocol
      |  networkProtocol: $networkProtocol
      |  serverName: $serverName
      |  clientName: $clientName
      |  mapName: $mapName
      |  gameDirectory: $gameDirectory
      |  playbackTime: $playbackTime
      |  playbackTicks: $playbackTicks
      |  playbackFrames: $playbackFrames
      |  signonLength: $signonLength
      |}""".stripMargin
}
