package com.azavea.busplan.routing

import java.io._

/**
 * Object used to read and write simple serializable java objects
 */
object Serialization {
  def deserialize[T](bytes: Array[Byte]): T = {
    val bis = new ByteArrayInputStream(bytes)
    try {
      val in = new ObjectInputStream(bis)
      in.readObject().asInstanceOf[T]
    } finally {
      bis.close()
    }
  }

  def read[T](path: String): T =
    deserialize[T](FilesystemUtils.read(path))

  def serialize[T](obj: T): Array[Byte] = {
    val bos = new ByteArrayOutputStream()
    try {
      val out = new ObjectOutputStream(bos)
      out.writeObject(obj)
      out.flush()
      bos.toByteArray()
    } finally {
      bos.close()
    }
  }

  def write[T](path: String, obj: T): Unit =
    FilesystemUtils.writeBytes(path, serialize(obj))
}
