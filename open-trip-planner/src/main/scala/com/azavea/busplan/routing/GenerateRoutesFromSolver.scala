package com.azavea.busplan.routing

import com.vividsolutions.jts.geom.Coordinate
import org.geotools.referencing._
import org.opentripplanner.common.model.GenericLocation
import org.opentripplanner.routing.core.RoutingRequest
import org.opentripplanner.routing.core.State
import org.opentripplanner.routing.graph.Edge
import org.opentripplanner.routing.graph.Graph
import org.opentripplanner.routing.graph.Vertex
import org.opentripplanner.routing.impl.GraphPathFinder
import org.opentripplanner.routing.spt.GraphPath
import org.opentripplanner.routing.error.TrivialPathException
import org.opentripplanner.standalone.Router

import com.vividsolutions.jts.geom.Coordinate
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io._
import scala.math
import scala.collection.JavaConverters._

/** Create a csv of routes from solver output */
object GenerateRoutesFromSolver {

  /**
   * Create a csv of actual bus routes from solver output
   * of stop sequences
   *
   * @param args(0) Cost matrix nodes csv
   * @param args(1) Solver output csv
   * @param args(2) Solver student assignment
   * @param args(3) Graph w/o highway access
   * @param args(4) Graph w/ highway access
   * @param args(5) Path to output csv
   */
  def main(args: Array[String]): Unit = {
    val nodes = FileInput.readNodes(args(0))
    val solverOutput = FileInput.readSolverOutput(args(1))
    val studentCounts = FileInput.readSolverStudentAssignment(args(2))
    val withStudents = RouteGraph.loadGraph(args(3))
    val withoutStudents = RouteGraph.loadGraph(args(4))
    val busRouter = new RouteGenerator(withStudents, withoutStudents,
      "CAR", true)
    val garageRouter = new RouteGenerator(withoutStudents, withoutStudents,
      "CAR", false)
    val headers = List("route_id", "origin_id", "destination_id", "route_sequence", "stop_sequence", "time", "x", "y")
    val writer = FileOutput.initializeCsv(args(5), headers)
    solverOutput.foreach {
      case (key, value) => routeOneBus(key, value, studentCounts, nodes, busRouter, garageRouter, writer)
    }
  }

  def getBellTime(routeStops: List[String], nodes: Map[String, Node]): Long = {
    nodes(routeStops.last).time
  }

  def routeOneBus(
    bus: String,
    routeStops: List[String],
    studentCounts: Map[(String, String), Int],
    nodes: Map[String, Node],
    busRouter: RouteGenerator,
    garageRouter: RouteGenerator,
    writer: BufferedWriter
  ): Unit = {
    var time = getBellTime(routeStops, nodes)
    var totalStudents = 0
    for (i <- (1 to (routeStops.size - 1)).reverse) {
      val origin = nodes(routeStops(i - 1))
      val destination = nodes(routeStops(i))
      busRouter.getRoute(bus, origin, destination, time, i) match {
        case Some(rv) =>
          if (i != 1) {
            val numStudents = studentCounts((bus, routeStops(i - 1)))
            totalStudents += numStudents
            time = rv(0).time - (45 + (10 * (numStudents - 1)))
          } else {
            time = rv(0).time
          }
          FileOutput.writeRoute(rv, writer)
        case None => ()
      }
    }

    // After routing all the way to the school separately route to
    // back to the stop using 'depart by'
    val school = nodes(routeStops(routeStops.length - 1))
    val garage = nodes(routeStops(0))
    // wait ten seconds per student to get off the bus
    val bellTime = getBellTime(routeStops, nodes) + 45 + (10 * (totalStudents - 1))
    val finalVertices = garageRouter.getRoute(bus, school, garage, bellTime, routeStops.size)
    if (finalVertices != None) {
      FileOutput.writeRoute(finalVertices.get, writer)
    }
  }
}
