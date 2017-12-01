package com.azavea.busplan.routing

import com.vividsolutions.jts.geom.Coordinate
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io._
import scala.collection.JavaConverters._

object fileOutput {

  // def appendRow(
  //   key: (String, String),
  //   value: RouteCost,
  //   writer: BufferedWriter): Unit = {
  //   writer.write(key._1 + "," + key._2 + "," + value.duration + "," + value.distance)
  //   writer.newLine()
  //   writer.flush()
  // }

  // def initializeCsv(filePath: String): BufferedWriter = {
  //   val csv = new FileWriter(filePath, true)
  //   val bw = new BufferedWriter(csv)
  //   bw.write("origin_id" + "," + "destination_id" + "," + "time" + "," + "distance")
  //   bw.newLine()
  //   bw.flush()
  //   bw
  // }

  // def writeCsv(
  //   filePath: String,
  //   results: Map[(String, String), RouteCost]): Unit = {
  //   val bw = initializeCsv(filePath)
  //   results.foreach { case (key, value) => appendRow(key, value, bw) }
  // }
}