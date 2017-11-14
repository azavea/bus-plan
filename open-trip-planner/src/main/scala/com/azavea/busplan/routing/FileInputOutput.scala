package com.azavea.busplan.routing

import com.vividsolutions.jts.geom.Coordinate
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io._
import scala.collection.JavaConverters._

object CsvIo {

  def parseCoordinate(record: Seq[String]): Coordinate = {
    val x = record(2).toDouble
    val y = record(3).toDouble
    new Coordinate(x, y)
  }

  def parseRecord(row: CSVRecord): Location = {
    val record = row
      .asScala
      .toList
    val coord = parseCoordinate(record)
    val isGarage = false
    val garage = if (record(4) == "garage") {
      val isGarage = true
    }
    Location(record(0), isGarage, coord)
  }

  def readCsv(filePath: String): Seq[Location] = {
    val csv = new FileReader(filePath)
    val records = CSVFormat.EXCEL
      .withHeader()
      .parse(csv)
      .getRecords()
      .asScala
      .map { record => parseRecord(record) }
    records
  }

  def writeCsv(
    filePath: String,
    results: Map[(String, String), RouteCost]): Unit = {
    val csv = new FileWriter(filePath, true)
    val bw = new BufferedWriter(csv)
    bw.write("origin_id" + "," + "destination_id" + "," + "time" + "," + "distance")
    bw.newLine()
    bw.flush()
    results.foreach { case (key, value) => appendRow(key, value, bw) }
  }

  def appendRow(
    key: (String, String),
    value: RouteCost,
    writer: BufferedWriter): Unit = {
    writer.write(key._1 + "," + key._2 + "," + value.duration + "," + value.distance)
    writer.newLine()
    writer.flush()
  }
}