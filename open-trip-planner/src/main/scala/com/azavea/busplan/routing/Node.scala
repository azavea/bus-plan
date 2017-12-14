package com.azavea.busplan.routing

import com.vividsolutions.jts.geom.Coordinate

case class Node(id: String, garage: Boolean, coord: Coordinate, time: Int)
