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

class RouteGenerator(withStudentGraph: Graph, withoutStudentGraph: Graph,
  mode: String, arriveBy: Boolean) {

  def calculateCost(route: GraphPath): RouteCost = {
    val distance = route.edges
      .asScala
      .map { edge => edge.getDistance }
      .sum
    new RouteCost(route.getDuration, distance)
  }

  def checkForStudents(start: Node, end: Node): Boolean = {
    if (start.garage || end.garage) {
      true
    } else {
      false
    }
  }

  def getCost(start: Node,
    end: Node,
    time: Long): RouteCost = {
    val route = getRoute(start, end, time)
    calculateCost(route)
  }

  def getRoute(start: Node,
    end: Node,
    time: Long): GraphPath = {
    val routingRequest = new RoutingRequest(mode)
    val startCoordinate = start.coord
    val endCoordinate = end.coord

    // routingRequest.numItineraries = 1
    routingRequest.arriveBy = arriveBy
    routingRequest.dateTime = math.abs(time)
    routingRequest.from = new GenericLocation(startCoordinate.x, startCoordinate.y)
    routingRequest.to = new GenericLocation(endCoordinate.x, endCoordinate.y)

    val hasStudents = checkForStudents(start, end)
    val targetGraph = if (hasStudents) {
      withStudentGraph
    } else {
      withoutStudentGraph
    }

    routingRequest.setRoutingContext(targetGraph)
    val router = new Router("TEST", targetGraph)
    val paths = new GraphPathFinder(router).getPaths(routingRequest)
    // TODO: put in try-catch block to catch TrivialPathException
    paths.get(0)
  }
}
