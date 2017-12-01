package com.azavea.busplan.routing

import java.io._
import java.nio.ByteBuffer
import java.nio.channels.FileChannel.MapMode._

/**
 * Taken from GeoTrellis Filesystem.scala:
 * https://github.com/locationtech/geotrellis/blob/7da2a70a8f27d8f13e9caa382d480270237c07e5/util/src/main/scala/geotrellis/util/Filesystem.scala
 */
object FilesystemUtils {
  /**
   * Write the given array of bytes to the file pointed to by the
   * given path.  This is a truncating write.
   *
   * @param path  The path to the file where the data are to be written
   * @param bytes An array of bytes containing the data to be written
   */
  def writeBytes(path: String, bytes: Array[Byte]): Unit = {
    val bos = new BufferedOutputStream(new FileOutputStream(path))
    bos.write(bytes)
    bos.close
  }

  /**
   * Read the contents of a file into an array.
   *
   * @param   path The path to the file to be read
   * @param   bs   The block size; The file will be read in chunks of this size
   * @return       An array of bytes containing the file contents
   */
  def read(path: String, bs: Int = (1 << 18)): Array[Byte] = {
    val buffer = toMappedByteBuffer(path)

    // read 256KiB (2^18 bytes) at a time out of the buffer into our array
    var i = 0
    val data = Array.ofDim[Byte](buffer.capacity)
    while (buffer.hasRemaining()) {
      val n = math.min(buffer.remaining(), bs)
      buffer.get(data, i, n)
      i += n
    }
    data
  }

  /**
   * Read the contents of a file into a MappedByteBuffer.
   *
   * @param   path The path to the file to be read
   * @return       A MappedByteBuffer containing the mapped file contents
   */
  def toMappedByteBuffer(path: String): ByteBuffer = {
    val f = new File(path)
    val fis = new FileInputStream(f)
    val size = f.length.toInt
    val channel = fis.getChannel
    val buffer = channel.map(READ_ONLY, 0, size)
    channel.close()
    fis.close()

    buffer
  }
}
