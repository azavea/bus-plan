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

import java.io._;
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

  def calculateRoute(start: Node,
    end: Node,
    time: Long): Option[GraphPath] = {
    val routingRequest = new RoutingRequest(mode)
    val startCoordinate = start.coord
    val endCoordinate = end.coord

    routingRequest.setArriveBy(this.arriveBy)
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

  def getCostToEachNode(
    referenceNodeKey: String,
    nodes: Map[String, Node],
    writer: BufferedWriter): Map[(String, String), RouteCost] = {
    val referenceNode = nodes(referenceNodeKey)
    val measureNodes = nodes - referenceNodeKey
    val costs = measureNodes.map {
      case (key, value) =>
        {
          val routeCost = getCost(referenceNode, value, 1512547200)
          val outputRow = List(referenceNodeKey, key, routeCost.duration, routeCost.distance)
          FileOutput.appendRow(outputRow, writer)
          Map((referenceNodeKey, key) -> routeCost)
        }
    }.reduce { (map1, map2) => map1 ++ map2 }
    costs
  }

  def getCostMap(start: Node,
    end: Node,
    time: Long): Map[(String, String), RouteCost] = {
    Map((start.id, end.id) -> getCost(start, end, time))
  }

  def getRoute(
    bus: String,
    start: Node,
    end: Node,
    time: Long,
    routeSequence: Int
  ): Option[List[RouteVertex]] =
    calculateRoute(start, end, time).map { route =>
      getStates(bus, route, routeSequence, start.id, end.id)
    }

  def getStates(bus: String,
    route: GraphPath,
    routeSequence: Int,
    originId: String,
    destinationId: String): Option[List[RouteVertex]] = {
    val states = route.states.asScala.toList
    val sm = states.map { state =>
      stateToRouteVertex(bus, originId,
        destinationId, routeSequence, states, state)
    }
      .toList
    Some(sm)
  }

  def stateToRouteVertex(bus: String,
    originId: String,
    destinationId: String,
    routeSequence: Int,
    states: List[State],
    state: State): RouteVertex = {
    val v = state.getVertex
    new RouteVertex(bus, originId, destinationId, routeSequence,
      states.indexOf(state), state.getTimeSeconds, v.getX, v.getY)
  }
}
