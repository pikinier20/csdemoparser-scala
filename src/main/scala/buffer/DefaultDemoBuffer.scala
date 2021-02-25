package demoparser
package buffer

import com.google.protobuf.CodedInputStream

import java.io.{BufferedInputStream, FileInputStream, InputStream}
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}

class DefaultDemoBuffer(private val input: CodedInputStream)
    extends DemoBuffer {
  // It will probably need some optimization
  def readInt: Int = input.readFixed32()
  def readUInt8: Int = input.readRawByte().toInt
  def readFloat: Float = input.readFloat()
  def readString(size: Int): String =
    new String(
      input.readRawBytes(size),
      StandardCharsets.UTF_8
    ).split('\u0000')(0)
  def skip(offset: Int): Unit = input.skipRawBytes(offset)
  def readIBytes: DemoBuffer = {
    val i = readInt
    DefaultDemoBuffer(input.readRawBytes(i))
  }
  def readBytes(size: Int): Array[Byte] = input.readRawBytes(size)
  def readVarint32: Int = input.readRawVarint32()
  def hasRemaining: Boolean = !input.isAtEnd
}

object DefaultDemoBuffer {
  def apply(in: InputStream): DefaultDemoBuffer =
    new DefaultDemoBuffer(CodedInputStream.newInstance(in))
  def apply(arr: Array[Byte]): DefaultDemoBuffer = {
    new DefaultDemoBuffer(CodedInputStream.newInstance(arr))
  }
  def apply(p: Path): DefaultDemoBuffer =
    new DefaultDemoBuffer(
      CodedInputStream
        .newInstance(new BufferedInputStream(Files.newInputStream(p)))
    )
}
