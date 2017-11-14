package com.azavea.busplan.routing

import com.google.common.collect.Maps
import org.opentripplanner.openstreetmap.impl.AnyFileBasedOpenStreetMapProviderImpl
import org.opentripplanner.graph_builder.module.osm.DefaultWayPropertySetSource
import org.opentripplanner.graph_builder.module.osm.OpenStreetMapModule
import org.opentripplanner.routing.graph.Graph
import org.opentripplanner.routing.graph.Graph.LoadLevel;

import java.io._

object RouteGraph {

  def main(args: Array[String]): Unit = {
    val sourcePath = args(0)
    val targetPath = args(1)
    val targetPaths = constructGraphNames(targetPath)
    build(sourcePath, true).save(new File(targetPaths(0)))
    build(sourcePath, false).save(new File(targetPaths(1)))
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

  def constructGraphNames(targetPath: String): Array[String] = {
    val graphName = targetPath.split("\\.")(0)
    val withName = graphName + "_withStudents.obj"
    val withOutName = graphName + "_withoutStudents.obj"
    Array(withName, withOutName)
  }

  def loadGraph(filePath: String): Graph = {
    val graphFile = new File(filePath)
    Graph.load(graphFile, LoadLevel.DEBUG)
  }
}
