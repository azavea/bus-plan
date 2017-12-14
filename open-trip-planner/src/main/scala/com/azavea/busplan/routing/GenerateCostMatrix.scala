package com.azavea.busplan.routing

import com.vividsolutions.jts.geom.Coordinate
import org.opentripplanner.routing.graph.Graph

import scala.collection.JavaConverters._

/** Measure network distance between each pair of a set of nodes */
object GenerateCostMatrix {

  /**
   * Given a set of nodes, calculate a cost matrix and write it
   * to a csv
   *
   * @param args(0) Cost matrix nodes csv
   * @param args(1) Graph w/o highway access
   * @param args(2) Graph w/ highway access
   * @param args(3) Path to output csv
   */
  def main(args: Array[String]): Unit = {
    val nodes = FileInput.readNodes(args(0))
    val withStudents = RouteGraph.loadGraph(args(1))
    val withoutStudents = RouteGraph.loadGraph(args(2))
    val busRouter = new RouteGenerator(withStudents, withoutStudents,
      "CAR", true)
    val headers = List("origin_id", "destination_id", "time", "distance")
    val writer = FileOutput.initializeCsv(args(3), headers)
    val costMatrix = nodes.keys.map { key =>
      busRouter.getCostToEachNode(
        key, nodes, writer)
    }
  }
}
