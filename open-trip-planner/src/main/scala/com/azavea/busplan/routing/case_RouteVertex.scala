package com.azavea.busplan.routing

case class RouteVertex(route: String, routeSequence: Int, stopSequence: Int,
  time: Int, x: Double, y: Double)