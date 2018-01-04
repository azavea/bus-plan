package com.azavea.busplan.routing

import org.scalatest._

class TEMPSpec extends FunSpec with Matchers {
  describe("boo") {
    it("yep") {
      val solverRoutes = FileInput.readSolverOutput("/Users/rob/proj/az/bus-plan/analysis/data/chester-output-jan-2/student-walk-0.40m_run0/OUTPUT_solver_routes.csv")
      val nodes = FileInput.readNodes("/Users/rob/data/bus-plan/input-updated-jan-2/cost_matrix_nodes.csv")
      val studentCounts = FileInput.readSolverStudentAssignment("/Users/rob/proj/az/bus-plan/analysis/data/chester-output-jan-2/student-walk-0.40m_run0/OUTPUT_solver_student_assignment.csv")

      // val g = RouteGraph.loadGraph("/Users/rob/data/bus-plan/input-updated-jan-2/graph_withStudents.obj")
      // val router = new RouteGenerator(g, g, "CAR", true)

      val costMatrix = FileInput.readCostMatrix("/Users/rob/data/bus-plan/input-updated-jan-2/cost_matrix.csv")



      val (crossRouteKids, crossRouteRideDuration) =
        solverRoutes.
          filter(!_._2.tail.isEmpty).
          map { case (routeId, stops) =>
            // println(s"   ROUTE $routeId")
            // println(stops.mkString(","))
            val (kids, totalRideDurations) =
              stops.
                drop(1).
                sliding(2).
                collect { case List(a, b) => (a, b) }.
                map { case (a, b) =>
                  val d = costMatrix.get((a, b)).getOrElse(RouteCost(0, 0.0)).duration
                  val sc = studentCounts((routeId, a))
                  (sc, d, a)
                }.
                toList.
                reverse.
                foldLeft((0, 0)) { case ((totalCount, totalDuration), (count, legDuration, stop)) =>
//                  println(s"$stop KIDS: $count TOTAL KIDS: $totalCount LEG DURATION: $legDuration TOTAL $totalDuration")
                  val kidsOnThisLeg = totalCount + count
                  (kidsOnThisLeg, totalDuration + (legDuration * kidsOnThisLeg))
                }

            val meanRideTime = ((totalRideDurations.toDouble / kids)).toInt

            val travelTimeFromCostMatrix =
              stops.
                drop(1).
                sliding(2).
                collect { case List(a, b) => (a, b) }.
                map { case (a, b) =>
                  costMatrix.get((a, b)).getOrElse(RouteCost(0, 0.0)).duration
                }.
                sum

            val pickupTime =
              stops.
                drop(1).
                reverse.
                drop(1).
                distinct.
                map { s =>
                  val sc = studentCounts((routeId, s))
                  45 + (sc - 1) * 10
                }.
                sum

            val numStudents =
              stops.
                drop(1).
                reverse.
                drop(1).
                distinct.
                map { s =>studentCounts((routeId, s)) }.
                sum

            //          val totalTime = travelTime + pickupTime
            val totalTime = travelTimeFromCostMatrix + pickupTime

            //(routeId, totalTime)
            println(s"      ${routeId} => ${totalTime/60} m (${numStudents} students) [mean ${meanRideTime/60}]")
            (kids, totalRideDurations)
          }.
          reduce { (a, b) => (a._1 + b._1, a._2 + b._2) }

      println(s"TOTAL KIDS: $crossRouteKids")
      println(s"MEAN DURATION: ${(crossRouteRideDuration.toDouble / crossRouteKids) / 60} minutes")

      // solverRoutes.
      //   foreach { case (k, vs) => println(s"${k} = ${vs.length}") }
      // println(s"HAAA ${solverRoutes.flatMap(_._2).toSet.size}")
      // solverRoutes.
      //   foreach { case (k, vs) => println(s"${k} = ${vs.length}") }
      // println(s"HfAAAAA ${solverRoutes.flatMap(_._2).toSet.size}")

      // // Are we picking up all kids?
      // println(s"TOTAL ASSIGNED STUDENTS: ${studentCounts.map(_._2).sum}")

      // // Are all stops routed?
      // val routedStops =
      //   solverRoutes.
      //     flatMap { case (r, sps) =>
      //       sps.map { s => (r, s) }
      //     }.
      //     toSet

      // println(solverRoutes.toList)
      // println( solverRoutes.
      //     flatMap { case (r, sps) =>
      //       sps.toList.map { s => (r, s) }
      //     }.size)
      // println(s"ROUTE STOPS?: ${solverRoutes.map(_._2.size).sum}")
      // println(s"ROUTE STOPS???: ${solverRoutes.flatMap(_._2).size}")
      // println(s"ROUTE STOPS??????: ${solverRoutes.flatMap { case (k, vs) => vs.map { v => (k, v) } }.toSet.size}")
      // println(s"ROUTE STOPS: ${routedStops.size}")

      // val assignedStops = studentCounts.keys.toSet

      // // println(assignedStops contains ("1","stop_3055424"))
      // // println(routedStops contains ("1","stop_3055424"))
      // // println(routedStops)
      // println(s"${assignedStops diff routedStops}")
    }
  }
}
