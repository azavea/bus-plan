package com.azavea.busplan.routing

import com.google.common.collect.Maps
import org.opentripplanner.openstreetmap.impl.AnyFileBasedOpenStreetMapProviderImpl
import org.opentripplanner.graph_builder.module.osm.DefaultWayPropertySetSource
import org.opentripplanner.graph_builder.module.osm.OpenStreetMapModule
import org.opentripplanner.routing.graph.Graph

import java.io._

object RouteGraph {
  def main(args: Array[String]): Unit = {
    val sourcePath = args(0)
    val targetPath = args(1)
    build(sourcePath, true).save(new File(targetPath))
  }

  def build(filePath: String, hasStudents: Boolean): Graph = {
    val loader = new OpenStreetMapModule()

    val data = new File(filePath)
    val g = new Graph()

    if (hasStudents) {
      loader.setDefaultWayPropertySetSource(new BusPlanWayPropertySet())
    } else {
      loader.setDefaultWayPropertySetSource(new DefaultWayPropertySetSource())
    }

    val provider = new AnyFileBasedOpenStreetMapProviderImpl()
    provider.setPath(data)
    loader.setProvider(provider)
    loader.buildGraph(g, Maps.newHashMap())

    g
  }
}
