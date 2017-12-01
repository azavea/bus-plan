package com.azavea.busplan.routing

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io._
import scala.collection.JavaConverters._

object StudentToStopMatrix {

  def main(args: Array[String]): Unit = {
    val stopToLocation = locationMap(args(0))
    val studentToLocation = locationMap(args(1))
    val studentToPossibleStops = CsvIo.readEligibleStops(args(2))
    val walkRouter = new WalkRouteCost(RouteGraph.loadGraph(args(3)))
    val results = routeAllStudents(studentToPossibleStops, stopToLocation,
      studentToLocation, walkRouter)
    Serialization.write(args(4), results)
    val studentToInfo = CsvIo.readStudentInfo(args(1))
    createStudentToStopCSV("/home/azavea/files/da_customer/bus_routing/data-wrangling/cost-matrix/student-stop-eligibility-25.csv",
      results, 1320, studentToInfo)
    createStudentToStopCSV("/home/azavea/files/da_customer/bus_routing/data-wrangling/cost-matrix/student-stop-eligibility-40.csv",
      results, 2112, studentToInfo)
    createStudentToStopCSV("/home/azavea/files/da_customer/bus_routing/data-wrangling/cost-matrix/student-stop-eligibility-50.csv",
      results, 2640, studentToInfo)
    createStudentToStopCSV("/home/azavea/files/da_customer/bus_routing/data-wrangling/cost-matrix/student-stop-eligibility-100.csv",
      results, 5280, studentToInfo)
  }

  def createStudentToStopCSV(
    path: String,
    results: Map[String, List[(String, Double)]],
    maxDistance: Double,
    studentToInfo: Map[String, (Int, String)]): Unit = {
    val csv = new FileWriter(path, true)
    val bw = new BufferedWriter(csv)
    for ((k, v) <- results) {
      bw.write(k)
      val newMaxDistance = getMaxDistance(maxDistance, studentToInfo(k)._1)
      val eligibleStops = getStopsBelowThreshold(v, newMaxDistance)
      for (stop <- eligibleStops) {
        bw.write("," + stop)
      }
      bw.newLine()
      bw.flush()
    }
  }

  def getMaxDistance(maxDistance: Double, grade: Int): Double = {
    if (grade < 7) {
      if (maxDistance > 2640) {
        val maxDistance = 2640
      }
    }
    maxDistance
  }

  def getStopsBelowThreshold(
    stopCosts: List[(String, Double)],
    maxDistance: Double): List[String] = {
    stopCosts
      .filter(s => s._2 < maxDistance)
      .map { s => s._1 }
  }

  def locationMap(filePath: String): Map[String, Location] = {
    val nodes = CsvIo.readCsv(filePath)
    sequenceToMap(nodes)
  }

  def routeAllStudents(
    studentToPossibleStops: Map[String, List[String]],
    stopToLocation: Map[String, Location],
    studentToLocation: Map[String, Location],
    walkRouter: WalkRouteCost): Map[String, List[(String, Double)]] = {
    studentToPossibleStops
      .map {
        case (key, value) => routeToEachEligible(key, value,
          stopToLocation, studentToLocation, walkRouter)
      }
      .reduce { (map1, map2) => map1 ++ map2 }
  }

  def routeToEachEligible(
    studentId: String,
    possibleStops: List[String],
    stopToLocation: Map[String, Location],
    studentToLocation: Map[String, Location],
    walkRouter: WalkRouteCost): Map[String, List[(String, Double)]] = {
    val studentLocation = studentToLocation(studentId)
    val costList = possibleStops
      .map { stop =>
        getWalkCost(stop, studentLocation, stopToLocation,
          walkRouter)
      }
      .toList
    Map(studentId -> costList)
  }

  def getWalkCost(oneStop: String,
    studentLocation: Location,
    stopToLocation: Map[String, Location],
    walkRouter: WalkRouteCost): (String, Double) = {
    val stopLocation = stopToLocation(oneStop)
    val distance = walkRouter.calculateCost(studentLocation.coord,
      stopLocation.coord, 1510560000).distance
    (oneStop, distance)
  }

  def sequenceToMap(nodes: Seq[Location]): Map[String, Location] = {
    nodes
      .map { node => Map(node.id -> node) }
      .reduce { (map1, map2) => map1 ++ map2 }
  }
}
