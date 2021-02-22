package demoparser
package io

import demoparser.io.DemoBuffer.MAX_OSPATH

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}

// This class is an abstraction over ByteBuffer to allow fast operations like readUInt32 etc.
class DemoBuffer(private val buffer: ByteBuffer) {
  // It will probably need some optimization
  def readInt: Int = buffer.getInt
  def readFloat: Float = buffer.getFloat
  def readMaxString: String = readString(MAX_OSPATH)
  def readString(size: Int): String =
    new String(
      Range(0, size).map(_ => buffer.get).toArray,
      StandardCharsets.UTF_8
    ).split('\u0000')(0)
}

object DemoBuffer {
  private val MAX_OSPATH = 260

  def apply(path: Path): DemoBuffer =
    new DemoBuffer(ByteBuffer.wrap(Files.readAllBytes(path)))
}
