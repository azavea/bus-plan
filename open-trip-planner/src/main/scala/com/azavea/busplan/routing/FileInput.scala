package com.azavea.busplan.routing

import com.vividsolutions.jts.geom.Coordinate
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io._
import scala.collection.JavaConverters._

object FileInput {

  def openCsv(filePath: String, header: Boolean): Seq[CSVRecord] = {
    val csv = new FileReader(filePath)
    if (header) {
      CSVFormat.EXCEL
        .withHeader()
        .parse(csv)
        .getRecords()
        .asScala
        .toList
    } else {
      CSVFormat.EXCEL
        .parse(csv)
        .getRecords()
        .asScala
        .toList
    }
  }

  def readEligibleStops(filePath: String): Map[String, List[String]] = {
    openCsv(filePath, false)
      .map { r => Map(r.asScala.toList(0) -> r.asScala.toList.drop(2)) }
      .reduce { (map1, map2) => map1 ++ map2 }
  }

  def readSolverOutput(filePath: String): List[(String, List[String])] = {
    openCsv(filePath, false)
      .toList
      .map { r => (r.get(0), List(r.asScala.toSeq:_*).tail) }
  }

  def readCostMatrix(filePath: String): Map[(String, String), RouteCost] =
    openCsv(filePath, true).
      map { r =>
        (r.get(0), r.get(1)) -> RouteCost(r.get(2).toInt, r.get(3).toDouble)
      }.
      toMap

  def readSolverStudentAssignment(filePath: String): Map[(String, String), Int] = {
    openCsv(filePath, false).
      map { r =>
        (r.get(0), r.get(1)) -> (r.size - 2)
      }.
      toMap
  }

  def parseCoordinate(record: Vector[String]): Coordinate = {
    val x = record(2).toDouble
    val y = record(3).toDouble
    new Coordinate(x, y)
  }

  def parseNode(row: CSVRecord): (String, Node) = {
    val record = row.asScala.toVector
    val coord = parseCoordinate(record)
    val isGarage = record(4) == "garage"
    record(0) -> new Node(record(0), isGarage, coord, record(5).toInt)
  }

  def readNodes(filePath: String): Map[String, Node] = {
    openCsv(filePath, true)
      .map { record => parseNode(record) }
      .toMap
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
