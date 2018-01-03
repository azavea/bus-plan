package com.azavea.busplan.routing

import com.vividsolutions.jts.geom.Coordinate
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io._
import scala.collection.JavaConverters._

object FileInput {

  def openCsv(filePath: String,
    header: Boolean): Seq[CSVRecord] = {
    val csv = new FileReader(filePath)
    if (header) {
      CSVFormat.EXCEL
        .withHeader()
        .parse(csv)
        .getRecords()
        .asScala
    } else {
      CSVFormat.EXCEL
        .parse(csv)
        .getRecords()
        .asScala
    }
  }

  def readEligibleStops(filePath: String): Map[String, List[String]] = {
    openCsv(filePath, false)
      .map { r => Map(r.asScala.toList(0) -> r.asScala.toList.drop(2)) }
      .reduce { (map1, map2) => map1 ++ map2 }
  }

  def readSolverOutput(filePath: String): Map[String, List[String]] = {
    openCsv(filePath, false)
      .map { r => Map(r.asScala.toList(0) -> r.asScala.toList.drop(1)) }
      .reduce { (map1, map2) => map1 ++ map2 }
  }

  def readSolverStudentAssignment(filePath: String): Map[(String, String), Int] = {
    openCsv(filePath, false)
      .map { r => Map((r.asScala.toList(0), r.asScala.toList(1)) -> (r.asScala.toList.size - 2)) }
      .reduce { (map1, map2) => map1 ++ map2 }
  }

  def parseCoordinate(record: Seq[String]): Coordinate = {
    val x = record(2).toDouble
    val y = record(3).toDouble
    new Coordinate(x, y)
  }

  def parseNode(row: CSVRecord): Map[String, Node] = {
    val record = row.asScala.toList
    val coord = parseCoordinate(record)
    val isGarage = false
    val garage = if (record(4) == "garage") {
      val isGarage = true
    }
    Map(record(0) -> new Node(record(0), isGarage, coord, record(5).toInt))
  }

  def readNodes(filePath: String): Map[String, Node] = {
    openCsv(filePath, true)
      .map { record => parseNode(record) }
      .reduce { (map1, map2) => map1 ++ map2 }
  }

  def parseStudentInfo(row: CSVRecord): Map[String, (Int, String)] = {
    val record = row.asScala.toList
    val info = (record(1).toInt, record(6))
    Map(record(0) -> info)
  }

  def readStudentInfo(filePath: String): Map[String, (Int, String)] = {
    openCsv(filePath, true)
      .map { record => parseStudentInfo(record) }
      .reduce { (map1, map2) => map1 ++ map2 }
  }
}