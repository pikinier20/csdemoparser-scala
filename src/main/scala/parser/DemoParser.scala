package demoparser
package parser

import demoparser.io.DemoBuffer
import demoparser.model.{Demo, Header}

import java.nio.file.Path

class DemoParser {
  def parseFromPath(path: Path): Either[String, Demo] = {
    val buffer = DemoBuffer(path)
    val header = parseHeader(buffer)
    println(header)
    ???
  }

  private def parseHeader(buffer: DemoBuffer): Header = {
    val magic = buffer.readString(8)
    val protocol = buffer.readInt
    val networkProtocol = buffer.readInt
    val serverName = buffer.readMaxString
    val clientName = buffer.readMaxString
    val mapName = buffer.readMaxString
    val gameDirectory = buffer.readMaxString
    val playbackTime = buffer.readFloat
    val playbackTicks = buffer.readInt
    val playbackFrames = buffer.readInt
    val signonLength = buffer.readInt
    Header(
      magic,
      protocol,
      networkProtocol,
      serverName,
      clientName,
      mapName,
      gameDirectory,
      playbackTime,
      playbackTicks,
      playbackFrames,
      signonLength
    )
  }
}
