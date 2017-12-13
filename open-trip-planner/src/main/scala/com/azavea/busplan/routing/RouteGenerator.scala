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

  def calculateCost(route: Option[GraphPath]): RouteCost = {
    if (route == None) {
      new RouteCost(0, 0)
    } else {
      val routeGraphPath = route.get
      val distance = routeGraphPath.edges
        .asScala
        .map { edge => edge.getDistance }
        .sum
      new RouteCost(routeGraphPath.getDuration, distance)
    }
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
    val route = calculateRoute(start, end, time)
    calculateCost(route)
  }

  def getCostMap(start: Node,
    end: Node,
    time: Long): Map[(String, String), RouteCost] = {
    Map((start.id, end.id) -> getCost(start, end, time))
  }

  def getRoute(bus: String,
    start: Node,
    end: Node,
    time: Long,
    routeSequence: Int): Option[List[RouteVertex]] = {
    val route = calculateRoute(start, end, time)
    if (route != None) {
      getStates(bus, route.get, routeSequence)
    } else {
      None
    }
  }

  def calculateRoute(start: Node,
    end: Node,
    time: Long): Option[GraphPath] = {
    val routingRequest = new RoutingRequest(mode)
    val startCoordinate = start.coord
    val endCoordinate = end.coord

    routingRequest.setArriveBy(true)
    routingRequest.dateTime = math.abs(time)
    routingRequest.from = new GenericLocation(startCoordinate.x, startCoordinate.y)
    routingRequest.to = new GenericLocation(endCoordinate.x, endCoordinate.y)

    val hasStudents = checkForStudents(start, end)
    val targetGraph = if (hasStudents) {
      withStudentGraph
    } else {
      withoutStudentGraph
    }

    try {
      routingRequest.setRoutingContext(targetGraph)
      val router = new Router("TEST", targetGraph)
      val paths = new GraphPathFinder(router).getPaths(routingRequest)
      Some(paths.get(0))
    } catch {
      case e: TrivialPathException => None
      case e: IndexOutOfBoundsException => None
    }
  }

  def getStates(bus: String,
    route: GraphPath,
    routeSequence: Int): Option[List[RouteVertex]] = {
    val states = route.states.asScala.toList
    val sm = states.map { state => stateToRouteVertex(bus, routeSequence, states, state) }
      .toList
    Some(sm)
  }

  def stateToRouteVertex(bus: String,
    routeSequence: Int,
    states: List[State],
    state: State): RouteVertex = {
    val v = state.getVertex
    new RouteVertex(bus, routeSequence, states.indexOf(state), state.getTimeSeconds, v.getX, v.getY)
  }
}
