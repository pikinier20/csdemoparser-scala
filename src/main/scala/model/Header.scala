package demoparser
package model

case class Header (
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
)
