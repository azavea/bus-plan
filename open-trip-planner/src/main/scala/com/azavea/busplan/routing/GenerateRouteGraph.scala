package com.azavea.busplan.routing

import com.google.common.collect.Maps
import org.opentripplanner.openstreetmap.impl.AnyFileBasedOpenStreetMapProviderImpl
import org.opentripplanner.graph_builder.module.osm.DefaultWayPropertySetSource
import org.opentripplanner.graph_builder.module.osm.OpenStreetMapModule
import org.opentripplanner.routing.graph.Graph
import org.opentripplanner.routing.graph.Graph.LoadLevel;

import java.io._

/** Construct graph objects */
object GenerateRouteGraph {

  /**
   * Generate two graph objects (one which allows highway access and
   * another that doesn't)
   *
   * @param args(0) road network data file (.osm.pbf)
   * @param args(1) output filepath
   */
  def main(args: Array[String]): Unit = {
    val sourcePath = args(0)
    val targetPath = args(1)
    val targetPaths = RouteGraph.constructGraphNames(targetPath)
    RouteGraph.build(sourcePath, true).save(new File(targetPaths(0)))
    RouteGraph.build(sourcePath, false).save(new File(targetPaths(1)))
  }
}
