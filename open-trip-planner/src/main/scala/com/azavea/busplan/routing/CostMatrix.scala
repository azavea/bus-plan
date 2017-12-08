package com.azavea.busplan.routing

import com.vividsolutions.jts.geom.Coordinate
import org.opentripplanner.routing.graph.Graph

import java.io._;
import scala.collection.JavaConverters._

object GenerateCostMatrix {

  def main(args: Array[String]): Unit = {
    val nodes = FileInput.readNodes(args(0))
    val withStudents = RouteGraph.loadGraph(args(1))
    val withoutStudents = RouteGraph.loadGraph(args(2))
    val busRouter = new RouteGenerator(withStudents, withoutStudents,
      "CAR", true)
    val headers = List("origin_id", "destination_id", "time", "distance")
    val writer = FileOutput.initializeCsv(args(3), headers)
    val costMatrix = nodes.keys.map { key => getCostToEachNode(key, nodes, busRouter, writer) }
  }

  // def getCost(
  //   busRouter: BusPlanRouteCost,
  //   origin: Location,
  //   destination: Location): Map[(String, String), RouteCost] = {
  //   val students = true
  //   if (origin.garage | destination.garage) {
  //     val students = false;
  //   }
  //   val cost = busRouter.calculateCost(origin.coord, destination.coord, 1510560000, students)
  //   Map((origin.id, destination.id) -> cost)
  // }

  // def getCostAndAppend(
  //   busRouter: BusPlanRouteCost,
  //   origin: Location,
  //   destination: Location,
  //   writer: BufferedWriter): Map[(String, String), RouteCost] = {
  //   val costMap = getCost(busRouter, origin, destination)
  //   val cost = costMap((origin.id, destination.id))
  //   CsvIo.appendRow((origin.id, destination.id), cost, writer)
  //   costMap
  // }

  def getCostToEachNode(
    referenceNodeKey: String,
    nodes: Map[String, Node],
    busRouter: RouteGenerator,
    writer: BufferedWriter): Map[(String, String), RouteCost] = {
    val referenceNode = nodes(referenceNodeKey)
    val measureNodes = nodes - referenceNodeKey
    val costs = measureNodes.map {
      case (key, value) =>
        {
          val routeCost = busRouter.getCost(referenceNode, value, 1512547200)
          val outputRow = List(referenceNodeKey, key, routeCost.duration, routeCost.distance)
          FileOutput.appendRow(outputRow, writer)
          Map((referenceNodeKey, key) -> routeCost)
        }
    }.reduce { (map1, map2) => map1 ++ map2 }
    costs
    //   // val locations = nodes
    //   //   .combinations(2)
    //   //   .flatMap { case Seq(x, y) => List((x, y), (y, x)) }
    //   //   .toList
    //   // val keys = locations
    //   //   .map { location => (location._1.id, location._2.id) }
    //   //   .toList
    //   // val costs = locations
    //   //   .map { location => getCostAndAppend(busRouter, location._1, location._2, bw) }
    //   //   .reduce { (map1, map2) => map1 ++ map2 }
    //   // costs
  }
}
