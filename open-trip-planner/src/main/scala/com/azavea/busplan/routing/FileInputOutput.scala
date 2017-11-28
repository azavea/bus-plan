package com.azavea.busplan.routing

import com.vividsolutions.jts.geom.Coordinate
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io._
import scala.collection.JavaConverters._

object CsvIo {

  def appendRow(
    key: (String, String),
    value: RouteCost,
    writer: BufferedWriter): Unit = {
    writer.write(key._1 + "," + key._2 + "," + value.duration + "," + value.distance)
    writer.newLine()
    writer.flush()
  }

  def initializeCsv(filePath: String): BufferedWriter = {
    val csv = new FileWriter(filePath, true)
    val bw = new BufferedWriter(csv)
    bw.write("origin_id" + "," + "destination_id" + "," + "time" + "," + "distance")
    bw.newLine()
    bw.flush()
    bw
  }

  def openCsv(filePath: String): Seq[CSVRecord] = {
    val csv = new FileReader(filePath)
    CSVFormat.EXCEL
      .withHeader()
      .parse(csv)
      .getRecords()
      .asScala
  }

  def parseCoordinate(record: Seq[String]): Coordinate = {
    val x = record(2).toDouble
    val y = record(3).toDouble
    new Coordinate(x, y)
  }

  def parseEligibleStops(row: CSVRecord): Map[String, List[String]] = {
    val record = row.asScala.toList
    Map(record(0) -> record.drop(2))
  }

  def parseRecord(row: CSVRecord): Location = {
    val record = row.asScala.toList
    val coord = parseCoordinate(record)
    val isGarage = false
    val garage = if (record(4) == "garage") {
      val isGarage = true
    }
    Location(record(0), isGarage, coord)
  }

  def parseStudentInfo(row: CSVRecord): Map[String, (Int, String)] = {
    val record = row.asScala.toList
    val info = (record(1).toInt, record(4))
    Map(record(0) -> info)
  }

  def readCsv(filePath: String): Seq[Location] = {
    openCsv(filePath).map { record => parseRecord(record) }
  }

  def readEligibleStops(filePath: String): Map[String, List[String]] = {
    openCsv(filePath)
      .map { record => parseEligibleStops(record) }
      .reduce { (map1, map2) => map1 ++ map2 }
  }

  def readStudentInfo(filePath: String): Map[String, (Int, String)] = {
    openCsv(filePath)
      .map { record => parseStudentInfo(record) }
      .reduce { (map1, map2) => map1 ++ map2 }
  }

  def writeCsv(
    filePath: String,
    results: Map[(String, String), RouteCost]): Unit = {
    val bw = initializeCsv(filePath)
    results.foreach { case (key, value) => appendRow(key, value, bw) }
  }
}