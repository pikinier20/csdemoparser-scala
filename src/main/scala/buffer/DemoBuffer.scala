package demoparser
package buffer

import com.google.protobuf.CodedInputStream
import demoparser.buffer.DemoBuffer.MAX_OSPATH

import java.io.{BufferedInputStream, File, FileInputStream}
import java.nio.{ByteBuffer, ByteOrder}
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}

// This class is an abstraction over ByteBuffer to allow fast operations like readUInt32 etc.
class DemoBuffer(private val input: CodedInputStream) {
  // It will probably need some optimization
  def readInt: Int = input.readFixed32()
  def readUInt8: Int = input.readRawByte().toInt
  def readFloat: Float = input.readFloat()
  def readMaxString: String = readString(MAX_OSPATH)
  def readString(size: Int): String =
    new String(
      input.readRawBytes(size),
      StandardCharsets.UTF_8
    ).split('\u0000')(0)
  def skip(offset: Int): Unit = input.skipRawBytes(offset)
  def readIBytes: DemoBuffer = {
    val i = readInt
    new DemoBuffer(CodedInputStream.newInstance(input.readRawBytes(i)))
  }
  def readBytes(size: Int): Array[Byte] = input.readRawBytes(size)
  def readVarint32: Int = input.readRawVarint32()
  def hasRemaining: Boolean = !input.isAtEnd
}

object DemoBuffer {
  private val MAX_OSPATH = 260

  def apply(path: Path): DemoBuffer = {
    new DemoBuffer(
      CodedInputStream.newInstance(
        new BufferedInputStream(Files.newInputStream(path))
      )
    )
  }
}
