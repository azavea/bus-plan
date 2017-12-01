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
    val bw = CsvIo.initializeCsv(args(3))
    val costMatrix = generateCostMatrix(nodes, busRouter, bw)
  }

  def getCost(
    busRouter: BusPlanRouteCost,
    origin: Location,
    destination: Location): Map[(String, String), RouteCost] = {
    val students = true
    if (origin.garage | destination.garage) {
      val students = false;
    }
    val cost = busRouter.calculateCost(origin.coord, destination.coord, 1510560000, students)
    Map((origin.id, destination.id) -> cost)
  }

  def getCostAndAppend(
    busRouter: BusPlanRouteCost,
    origin: Location,
    destination: Location,
    writer: BufferedWriter): Map[(String, String), RouteCost] = {
    val costMap = getCost(busRouter, origin, destination)
    val cost = costMap((origin.id, destination.id))
    CsvIo.appendRow((origin.id, destination.id), cost, writer)
    costMap
  }

  def generateCostMatrix(
    nodes: Seq[Location],
    busRouter: BusPlanRouteCost,
    bw: BufferedWriter): Map[(String, String), RouteCost] = {
    val locations = nodes
      .combinations(2)
      .flatMap { case Seq(x, y) => List((x, y), (y, x)) }
      .toList
    val keys = locations
      .map { location => (location._1.id, location._2.id) }
      .toList
    val costs = locations
      .map { location => getCostAndAppend(busRouter, location._1, location._2, bw) }
      .reduce { (map1, map2) => map1 ++ map2 }
    costs
  }
}
