package com.azavea.busplan.routing

case class RouteVertex(route: String, routeSequence: Int, stopSequence: Int,
  time: Long, x: Double, y: Double)