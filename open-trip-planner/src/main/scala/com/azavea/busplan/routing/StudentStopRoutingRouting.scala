package com.azavea.busplan.routing

object StudentToStopMatrix {
  def main(args: Array[String]): Unit = {
    val stopToLocation = locationMap(args(0))
    val studentToLocation = locationMap(args(1))
    val studentToPossibleStops = CsvIo.readEligibleStops(args(2))
    val walkRouter = new WalkRouteCost(RouteGraph.loadGraph(args(3)))

    // Incomplete - will throw runtime error
    val result = routeToEachEligible(stopToLocation, studentToLocation,
      studentToPossibleStops, walkRouter)
    // TODO: Serialization
    val studentToInfo = CsvIo.readStudentInfo(args(1))
  }

  def createStudentToStopCSV(
    path: String,
    results: Map[String, List[(String, Double)]],
    maxDistance: Int => Double): Unit = {
    ??? // TODO: given a max distance threshold write csv with eligible stops
  }

  def locationMap(filePath: String): Map[String, Location] = {
    val nodes = CsvIo.readCsv(filePath)
    sequenceToMap(nodes)
  }

  def routeToEachEligible(
    stops: Map[String, Location],
    students: Map[String, Location],
    possibleStops: Map[String, List[String]],
    walkRouter: WalkRouteCost): Map[String, List[(String, Double)]] = {
    ??? // TODO
  }

  def sequenceToMap(nodes: Seq[Location]): Map[String, Location] = {
    nodes
      .map { node => Map(node.id -> node) }
      .reduce { (map1, map2) => map1 ++ map2 }
  }
}
