package com.azavea.busplan.routing

import com.vividsolutions.jts.geom.Coordinate

case class Location(id: String, coord: Coordinate)

object CostMatrix {
  // TODO: How do we handle different times?
  /** Takes a sequence of locations, and produces a map of
    * location id tuples and the route cost that represents
    * the cost of the route from the first location to the second
    * location.
    */
  def generate(locations: Seq[Location]): Map[(String, String), RouteCost] = {
    val map: Map[(String, String), RouteCost] = ???

    val rc: RouteCost = map(("a", "b"))
    ???
  }

  // TODO: How to get a Seq[Location] from a csv
  // TODO: How to save off the data in a Map[(String, String), RouteCost] as a csv?
}
