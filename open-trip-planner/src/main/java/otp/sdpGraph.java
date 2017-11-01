/**
 *
 */
package sdpBusRoutingCostFunction;

import java.io.File;
import java.util.HashMap;

import org.opentripplanner.graph_builder.module.osm.DefaultWayPropertySetSource;
import org.opentripplanner.graph_builder.module.osm.OpenStreetMapModule;
import org.opentripplanner.openstreetmap.impl.AnyFileBasedOpenStreetMapProviderImpl;
import org.opentripplanner.routing.graph.Graph;

import com.google.common.collect.Maps;

/**
 * @author azavea
 *
 */
public class sdpGraph {

	String filePath;

	public static void main(String[] args) throws java.io.IOException {
    sdpGraph graph = new sdpGraph(args[0]);
    Graph g = graph.buildSdpGraph(true);
    g.save(new java.io.File(args[1]));
  }

	/**
	 * @param filePath
	 */
	public sdpGraph(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * @param hasStudents
	 * @return
	 */
	public Graph buildSdpGraph(boolean hasStudents) {

		OpenStreetMapModule loader = new OpenStreetMapModule();

		File data = new File(this.filePath);
		Graph g = new Graph();

		if (hasStudents) {
			loader.setDefaultWayPropertySetSource(new sdpWayPropertySet());
		} else {
			loader.setDefaultWayPropertySetSource(new DefaultWayPropertySetSource());
		}

		AnyFileBasedOpenStreetMapProviderImpl provider = new AnyFileBasedOpenStreetMapProviderImpl();
		provider.setPath(data);
		loader.setProvider(provider);
		HashMap<Class<?>, Object> extra = Maps.newHashMap();
		loader.buildGraph(g, extra);
		return g;
	}
}
