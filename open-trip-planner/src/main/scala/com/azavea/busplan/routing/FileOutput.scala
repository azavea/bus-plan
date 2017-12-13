package com.azavea.busplan.routing

import com.vividsolutions.jts.geom.Coordinate
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io._
import scala.collection.JavaConverters._

object FileOutput {

  def writeRoute(vertices: List[RouteVertex],
    writer: BufferedWriter): Unit = {
    vertices.foreach(v => appendVertex(v, writer))
  }

  def appendRow(values: List[Any], writer: BufferedWriter): Unit = {
    val stringValues = values.mkString(",")
    writer.write(stringValues)
    writer.newLine
    writer.flush
  }

  def appendVertex(vertex: RouteVertex,
    writer: BufferedWriter): Unit = {
    writer.write(vertex.route + "," + vertex.routeSequence + "," +
      vertex.stopSequence + "," + vertex.time + "," + vertex.x +
      "," + vertex.y)
    writer.newLine()
    writer.flush()
  }

  def initializeCsv(filePath: String, headers: List[String]): BufferedWriter = {
    val csv = new FileWriter(filePath, true)
    val writer = new BufferedWriter(csv)
    writer.write(headers.mkString(","))
    writer.newLine()
    writer.flush()
    writer
  }
}