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

import scala.math
import scala.collection.JavaConverters._

object GenerateRoutesFromSolver {

  def main(args: Array[String]): Unit = {

    val nodes = FileInput.readNodes(args(0))
    val solverOutput = FileInput.readSolverOutput(args(1))

    // val route1 = solverOutput("garage_1")
    // println(route1)
    // val bt = getBellTime(route1, nodes)
    // println(bt)

    val withStudents = RouteGraph.loadGraph(args(2))
    val withoutStudents = RouteGraph.loadGraph(args(3))
    val busRouter = new RouteGenerator(withStudents, withoutStudents,
      "CAR", true)
    val r = "garage_18"
    routeOneBus(r, solverOutput(r), nodes, busRouter)
    // val oneRoute = busRouter.getRoute(
    //   nodes("stop_3055442"),
    //   nodes("stop_3055433"),
    //   1512029400, true)
    // val sor = oneRoute.states.asScala
    // println(sor)
    // println(sor(0))
  }

  def getBellTime(routeStops: List[String],
    nodes: Map[String, Node]): Int = {
    nodes(routeStops.last).time
  }

  def routeOneBus(
    bus: String,
    routeStops: List[String],
    nodes: Map[String, Node],
    busRouter: RouteGenerator): Unit = {
    val time = getBellTime(routeStops, nodes)
    println(time)
    for (i <- (1 to routeStops.size - 1).reverse) {
      val origin = nodes(routeStops(i - 1))
      val destination = nodes(routeStops(i))
      val theRoute = busRouter.getRoute(origin, destination, time)
      println("O: " + origin.id, " D: ", destination.id)
      // println(destination.id)
    }
  }

  // def getRoute(start: Coordinate,
  //   end: Coordinate,
  //   time: Long,
  //   hasStudents: Boolean): Unit = {
  //   val routingRequest = new RoutingRequest("CAR")

  //   routingRequest.dateTime = math.abs(time)
  //   routingRequest.from = new GenericLocation(start.x, start.y)
  //   routingRequest.to = new GenericLocation(end.x, end.y)

  //   val targetGraph =
  //     if (hasStudents) {
  //       withStudentGraph
  //     } else {
  //       withoutStudentGraph
  //     }

  //   try {
  //     routingRequest.setRoutingContext(targetGraph)
  //     val router = new Router("TEST", targetGraph)
  //     val paths = new GraphPathFinder(router).getPaths(routingRequest)
  //     val route = paths.get(0)
  //     println(route)
  //   } catch {
  //     case e: TrivialPathException => println("TrivialPathException")
  //   }
  // }

  // def calculateCost(
  //   start: Coordinate,
  //   end: Coordinate,
  //   time: Long,
  //   hasStudents: Boolean): RouteCost = {
  //   val routingRequest = new RoutingRequest("CAR")

  //   routingRequest.dateTime = math.abs(time)
  //   routingRequest.from = new GenericLocation(start.x, start.y)
  //   routingRequest.to = new GenericLocation(end.x, end.y)

  //   val targetGraph =
  //     if (hasStudents) {
  //       withStudentGraph
  //     } else {
  //       withoutStudentGraph
  //     }

  //   try {
  //     routingRequest.setRoutingContext(targetGraph)
  //     val router = new Router("TEST", targetGraph)
  //     val paths = new GraphPathFinder(router).getPaths(routingRequest)
  //     val route = paths.get(0)

  //     val distance =
  //       route.edges
  //         .asScala
  //         .map { edge => edge.getDistance }
  //         .sum

  //     new RouteCost(route.getDuration, distance)
  //   } catch {
  //     case e: TrivialPathException => new RouteCost(0, 0)
  //   }
  // }
}
