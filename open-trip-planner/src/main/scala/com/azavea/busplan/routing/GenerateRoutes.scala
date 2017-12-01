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

object RouteGenerator {

  def main(args: Array[String]): Unit = {
    val nodes = StudentToStopMatrix.locationMap(args(0))
    val busRouter = new BusPlanRouteCost(
      RouteGraph.loadGraph(args(1)),
      RouteGraph.loadGraph(args(2)))
    val bellTimes = CsvIo.readBellTimes(args(4))

    val routes = CsvIo.readSolverOutput(args(3))
    println(routes)

    // val oneRoute = busRouter.getRoute(
    //   nodes("stop_3055442"),
    //   nodes("stop_3055433"),
    //   1512029400, true)
    // val sor = oneRoute.states.asScala
    // println(sor)
    // println(sor(0))
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
