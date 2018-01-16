package com.azavea.busplan.routing

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io._
import scala.collection.JavaConverters._

/** Measure walk distances for students to their stop within a plan */
object GenerateStudentWalkDistances {
  def main(args: Array[String]): Unit = {
    /**
     * Find costs for student-stop pairs and write to a csv
     *
     * @param args(0) Cost matrix nodes csv
     * @param args(1) Student nodes csv
     * @param args(2) student stop assignment
     * @param args(3) Graph w/o highway access
     * @param args(4) Path to output csv
     */
    val stopToLocation = FileInput.readNodes(args(0))
    val studentToLocation = FileInput.readNodes(args(1))

    val studentToRoutedStop = FileInput.readRoutedStop(args(2))

    val graph = RouteGraph.loadGraph(args(3))
    val walkRouter = new RouteGenerator(graph, graph, "WALK", false)
    val outputCsv = FileOutput.initializeCsv(args(4), List("student_id", "stop_id", "distance"))
    val eachCost = studentToRoutedStop.map {
      case (key, value) => {
        try {
          val studentLocation = studentToLocation(key)
          val stopLocation = stopToLocation(value)
          val cost = walkRouter.getCost(studentLocation, stopLocation, 1513168200).distance
          // TODO: rewrite as a function in FileOutput.scala
          outputCsv.write(key + "," + value + "," + cost)
          outputCsv.newLine()
          outputCsv.flush()
        } catch {
          case e: NoSuchElementException => None
        }
      }
    }
  }
}
