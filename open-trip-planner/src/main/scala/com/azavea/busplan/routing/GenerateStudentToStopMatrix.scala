package com.azavea.busplan.routing

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io._
import scala.collection.JavaConverters._

object GenerateStudentToStopMatrix {

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
    StudentToStopRouting.createStudentToStopCSV("/home/azavea/files/da_customer/bus_routing/data-wrangling/student-to-stop/eligibility/eligibility-25.csv", results, 1320, studentToInfo)
    StudentToStopRouting.createStudentToStopCSV("/home/azavea/files/da_customer/bus_routing/data-wrangling/cost-matrix/student-stop-eligibility-40.csv", results, 2112, studentToInfo)
    StudentToStopRouting.createStudentToStopCSV("/home/azavea/files/da_customer/bus_routing/data-wrangling/cost-matrix/student-stop-eligibility-50.csv", results, 2640, studentToInfo)
    StudentToStopRouting.createStudentToStopCSV("/home/azavea/files/da_customer/bus_routing/data-wrangling/cost-matrix/student-stop-eligibility-100.csv", results, 5280, studentToInfo)
  }
}
