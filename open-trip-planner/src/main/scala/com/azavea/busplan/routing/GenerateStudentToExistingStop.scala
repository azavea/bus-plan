package com.azavea.busplan.routing

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io._
import scala.collection.JavaConverters._

/** Measure walk distances for students to currently assigned stop */
object GenerateStudentToExistingStopCosts {

  /**
   * Find costs for student-stop pairs and write to a csv
   *
   * @param args(0) Cost matrix nodes csv
   * @param args(1) Student nodes csv
   * @param args(2) Graph w/o highway access
   * @param args(3) Path to output csv
   */
  def main(args: Array[String]): Unit = {
    val stopToLocation = FileInput.readNodes(args(0))
    val studentToLocation = FileInput.readNodes(args(1))
    val studentToInfo = FileInput.readStudentInfo(args(1))
    val graph = RouteGraph.loadGraph(args(2))
    val walkRouter = new RouteGenerator(graph, graph, "WALK", false)
    val outputCsv = FileOutput.initializeCsv(args(3), List("student_id", "stop_id", "time", "distance"))
    val eachCost = studentToInfo.map {
      case (key, value) => {
        val studentLocation = studentToLocation(key)
        val stopLocation = stopToLocation(value._2)
        val cost = walkRouter.getCost(studentLocation, stopLocation,
          Constants.DEFAULT_COST_TIME).distance
        // TODO: rewrite as a function in FileOutput.scala
        outputCsv.write(key + "," + value._2 + ",NA," + cost)
        outputCsv.newLine()
        outputCsv.flush()
      }
    }
  }
}
