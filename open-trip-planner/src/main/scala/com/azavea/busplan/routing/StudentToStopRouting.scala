package com.azavea.busplan.routing

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io._
import scala.collection.JavaConverters._

object StudentToStopRouting {

  /**
   * Find all stops that a student is allowed to given a maximum walk
   * distance threshold. Write these results to a csv.
   *
   * @param path            File path for output csv
   * @param results         Map of student IDs to stop ID-distance pairs
   * @param getMaxDistance  Function taking a grade level and returning a distance
   * @param studentToInfo   Map of student IDs and grade-existing stop ID pairs
   */
  def createStudentToStopCSV(
    path: String,
    results: Map[String, List[(String, Double)]],
    getMaxDistance: Int => Double,
    studentToInfo: Map[String, (Int, String)]): Unit = {
    val csv = new FileWriter(path, true)
    val bw = new BufferedWriter(csv)
    for ((k, v) <- results) {
      bw.write(k)
      // If a student's existing stop is more than 1.5 miles from 
      // her home, her only option is that stop
      if (v(0)._2 >= 2414) {
        bw.write("," + v(0)._1)
        bw.newLine()
        bw.flush()
      } else {
        var eligibleStops = getStopsBelowThreshold(v,
          getMaxDistance(studentToInfo(k)._1))
        for (stop <- eligibleStops) {
          bw.write("," + stop)
        }
        bw.newLine()
        bw.flush()
      }
    }
  }

  /**
   * Filter list of stop-cost pairs to only return the ids of stops
   * below a specified maximum distance threshold
   *
   * @param stopsCosts   List of (Stop ID, Distance to stop) tuples
   * @param maxDistance  Distance threshold
   */
  def getStopsBelowThreshold(
    stopCosts: List[(String, Double)],
    maxDistance: Double): List[String] = {
    stopCosts
      .filter(s => s._2 < maxDistance)
      .map { s => s._1 }
  }

  /**
   * Route all students to each stop within 1 mile of euclidean
   * distance of their home
   *
   * @param studentToPossibleStops  Map of student IDs and stops within a mile
   * @param stopToLocation          Map of stop IDs and location nodes
   * @param studentToLocation       Map of student IDs and home location nodes
   * @param walkRouter              RouteGenerator object configured for walking
   */
  def routeAllStudents(
    studentToPossibleStops: Map[String, List[String]],
    stopToLocation: Map[String, Node],
    studentToLocation: Map[String, Node],
    walkRouter: RouteGenerator): Map[String, List[(String, Double)]] = {
    studentToPossibleStops
      .map {
        case (key, value) => routeToEachEligibleStop(key, value,
          stopToLocation, studentToLocation, walkRouter)
      }
      .reduce { (map1, map2) => map1 ++ map2 }
  }

  /**
   * Route onr student to all of his eligible stops (called by
   * routeAllStudents)
   *
   * @param studentId               Students UUID
   * @param possibleStops           List of all stops within a mile of the student
   * @param stopToLocation          Map of stop IDs and location nodes
   * @param studentToLocation       Map of student IDs and home location nodes
   * @param walkRouter              RouteGenerator object configured for walking
   */
  def routeToEachEligibleStop(
    studentId: String,
    possibleStops: List[String],
    stopToLocation: Map[String, Node],
    studentToLocation: Map[String, Node],
    walkRouter: RouteGenerator): Map[String, List[(String, Double)]] = {
    val studentLocation = studentToLocation(studentId)
    val costList = possibleStops
      .map { stop =>
        (stop, walkRouter.getCost(studentLocation, stopToLocation(stop),
          1513168200).distance)
      }
      .toList
    Map(studentId -> costList)
  }
}
