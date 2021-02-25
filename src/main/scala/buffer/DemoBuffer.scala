package demoparser
package buffer

import com.google.protobuf.CodedInputStream

import java.io.{BufferedInputStream, File, FileInputStream}
import java.nio.{ByteBuffer, ByteOrder}
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}

// This class is an abstraction over ByteBuffer to allow fast operations like readUInt32 etc.
trait DemoBuffer {
  protected val MAX_OSPATH = 260
  def readInt: Int
  def readUInt8: Int
  def readFloat: Float
  def readMaxString: String = readString(MAX_OSPATH)
  def readString(size: Int): String
  def readBytes(size: Int): Array[Byte]
  def readVarint32: Int
  def hasRemaining: Boolean
  def readIBytes: DemoBuffer
  def skip(size: Int): Unit
}
