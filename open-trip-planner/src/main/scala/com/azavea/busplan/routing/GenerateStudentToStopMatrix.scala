package com.azavea.busplan.routing

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io._
import scala.collection.JavaConverters._

/** Find all stops within a certain distance of each student */
object GenerateStudentToStopMatrix {

  /**
   * Given datasets of student nodes and candidate stops, output
   * csvs with all eligible stops for each student at four different
   * walk distance thresholds
   *
   * @param args(0) Cost matrix nodes csv
   * @param args(1) Student nodes csv
   * @param args(2) Eligible stops csv
   * @param args(3) Graph w/o highway access
   * @param args(4) Path to output .ser file
   * @param args(5) Path to output csv
   */
  def main(args: Array[String]): Unit = {
    val stopToLocation = FileInput.readNodes(args(0))
    val studentToLocation = FileInput.readNodes(args(1))
    val studentToPossibleStops = FileInput.readEligibleStops(args(2))
    val graph = RouteGraph.loadGraph(args(3))
    val walkRouter = new RouteGenerator(graph, graph, "WALK", false)

    val results = StudentToStopRouting.routeAllStudents(studentToPossibleStops, stopToLocation,
      studentToLocation, walkRouter)
    Serialization.write(args(4), results)
    val studentToInfo = FileInput.readStudentInfo(args(1))
    val baseFileName = args(5).split("\\.")(0)

    StudentToStopRouting.createStudentToStopCSV(baseFileName + "-25.csv", results, 402, studentToInfo)
    StudentToStopRouting.createStudentToStopCSV(baseFileName + "-40.csv", results, 644, studentToInfo)
    StudentToStopRouting.createStudentToStopCSV(baseFileName + "-50.csv", results, 804, studentToInfo)
    StudentToStopRouting.createStudentToStopCSV(baseFileName + "-100.csv", results, 1609, studentToInfo)
  }
}
