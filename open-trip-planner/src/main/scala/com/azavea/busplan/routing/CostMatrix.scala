package com.azavea.busplan.routing

import com.vividsolutions.jts.geom.Coordinate
import org.opentripplanner.routing.graph.Graph

import java.io._;
import scala.collection.JavaConverters._

case class Location(id: String, garage: Boolean, coord: Coordinate)

object CostMatrix {

  def main(args: Array[String]): Unit = {
    val nodes = CsvIo.readCsv(args(0))
    val busRouter = new BusPlanRouteCost(
      RouteGraph.loadGraph(args(1)),
      RouteGraph.loadGraph(args(2)))
    val costMatrix = generateCostMatrix(nodes, busRouter)
    CsvIo.writeCsv(args(3), costMatrix)
  }

  def getCost(
    busRouter: BusPlanRouteCost,
    origin: Location,
    destination: Location): RouteCost = {
    val students = true
    if (origin.garage | destination.garage) {
      val students = false;
    }
    busRouter.calculateCost(origin.coord, destination.coord, 1510560000, students)
  }

  def generateCostMatrix(
    nodes: Seq[Location],
    busRouter: BusPlanRouteCost): Map[(String, String), RouteCost] = {
    val locations = nodes
      .combinations(2)
      .flatMap { case Seq(x, y) => List((x, y), (y, x)) }
      .toList
    val keys = locations
      .map { location => (location._1.id, location._2.id) }
      .toList
    val costs = locations
      .map { location => getCost(busRouter, location._1, location._2) }
      .toList
    val costMatrix = (keys zip costs).toMap
    costMatrix
  }
}
