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

class WalkRouteCost(walkGraph: Graph) {

  def calculateCost(
    start: Coordinate,
    end: Coordinate,
    time: Long): RouteCost = {
    val routingRequest = new RoutingRequest("WALK")

    routingRequest.dateTime = math.abs(time)
    routingRequest.from = new GenericLocation(start.x, start.y)
    routingRequest.to = new GenericLocation(end.x, end.y)

    try {
      routingRequest.setRoutingContext(walkGraph)
      val router = new Router("TEST", walkGraph)
      val paths = new GraphPathFinder(router).getPaths(routingRequest)
      val route = paths.get(0)

      val distance =
        route.edges
          .asScala
          .map { edge => edge.getDistance }
          .sum

      new RouteCost(route.getDuration, distance)
    } catch {
      case e: TrivialPathException => new RouteCost(0, 0)
    }
  }
}
