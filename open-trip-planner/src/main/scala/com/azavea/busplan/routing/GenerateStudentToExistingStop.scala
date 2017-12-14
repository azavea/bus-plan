package com.azavea.busplan.routing

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io._
import scala.collection.JavaConverters._

object GenerateStudentToExistingStopCosts {

  def main(args: Array[String]): Unit = {
    val stopToLocation = FileInput.readNodes(args(0))
    val studentToLocation = FileInput.readNodes(args(1))
    val studentToInfo = FileInput.readStudentInfo(args(1))
    val graph = RouteGraph.loadGraph(args(2))
    val walkRouter = new RouteGenerator(graph, graph, "WALK", false)
    val outputCsv = FileOutput.initializeCsv(args(3), List("student_id", "stop_id", "count", "distance"))
    val eachCost = studentToInfo.map {
      case (key, value) => {
        val studentLocation = studentToLocation(key)
        val stopLocation = stopToLocation(value._2)
        val cost = walkRouter.getCost(studentLocation, stopLocation, 1513168200).distance
        outputCsv.write(key + "," + value._2 + ",NA," + cost)
        outputCsv.newLine()
        outputCsv.flush()
      }
    }
  }
}
