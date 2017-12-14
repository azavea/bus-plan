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
   * @param args(2) Graph w/o highway access
   * @param args(3) Graph w/ highway access
   * @param args(4) Path to output csv
   */
  def main(args: Array[String]): Unit = {
    val nodes = FileInput.readNodes(args(0))
    val solverOutput = FileInput.readSolverOutput(args(1))
    val withStudents = RouteGraph.loadGraph(args(2))
    val withoutStudents = RouteGraph.loadGraph(args(3))
    val busRouter = new RouteGenerator(withStudents, withoutStudents,
      "CAR", true)
    val headers = List("route_id", "origin_id", "destination_id", "route_sequence", "stop_sequence", "time", "x", "y")
    val writer = FileOutput.initializeCsv(args(4), headers)
    solverOutput.foreach {
      case (key, value) => routeOneBus(key, value, nodes, busRouter, writer)
    }
  }

  def getBellTime(routeStops: List[String],
    nodes: Map[String, Node]): Long = {
    nodes(routeStops.last).time
  }

  def routeOneBus(
    bus: String,
    routeStops: List[String],
    nodes: Map[String, Node],
    busRouter: RouteGenerator,
    writer: BufferedWriter): Unit = {
    var time = getBellTime(routeStops, nodes)
    for (i <- (1 to routeStops.size - 1).reverse) {
      val origin = nodes(routeStops(i - 1))
      val destination = nodes(routeStops(i))
      val routeVertices = busRouter.getRoute(bus, origin, destination, time, i)
      if (routeVertices != None) {
        val rv = routeVertices.get
        time = rv(0).time
        FileOutput.writeRoute(rv, writer)
      }
    }
  }
}
