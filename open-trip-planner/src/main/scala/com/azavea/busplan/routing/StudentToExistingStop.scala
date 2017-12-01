package com.azavea.busplan.routing

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io._
import scala.collection.JavaConverters._

object StudentToExistingStop {
  def main(args: Array[String]): Unit = {
    val stopToLocation = StudentToStopMatrix.locationMap(args(0))
    val studentToLocation = StudentToStopMatrix.locationMap(args(1))
    val studentToInfo = CsvIo.readStudentInfo(args(1))
    val walkRouter = new WalkRouteCost(RouteGraph.loadGraph(args(2)))

    val outputCsv = CsvIo.initializeCsv(args(3))
    val eachCost = studentToInfo.map {
      case (key, value) => {
        val cost = StudentToStopMatrix.getWalkCost(value._2, studentToLocation(key),
          stopToLocation, walkRouter)
        outputCsv.write(key + "," + cost._1 + ",NA," + cost._2)
        outputCsv.newLine()
        outputCsv.flush()
        cost
      }
    }
  }
}
