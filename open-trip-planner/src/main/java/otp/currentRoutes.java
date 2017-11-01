/**
 * 
 */
package sdpBusRoutingCostFunction;

import java.io.File;
import java.io.IOException;

import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.graph.Graph.LoadLevel;

/**
 * @author azavea
 *
 */
public class currentRoutes {

	/**
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static void main(String[] args) throws ClassNotFoundException, IOException {

		File graphFile = new File(
				"/home/azavea/eclipse-workspace/sdpBusRoutingCostFunction/graph_highways_restricted.obj");
		Graph gg = Graph.load(graphFile, LoadLevel.DEBUG);

		costFunction c = new costFunction(true, gg);

		File dir = new File("/home/azavea/files/da_customer/bus_routing/map-routes/route-csvs-2/");
		File[] directoryListing = dir.listFiles();

		int i = 0;
		csvWriter csv = new csvWriter("/home/azavea/files/da_customer/bus_routing/output.csv");

		for (File child : directoryListing) {
			System.out.println(child.toString());
			routeFullCsv.routeCsv(c, child.toString(), csv, true);
			i++;
			System.out.println("completed: [" + i + "] of " + directoryListing.length);
		}
	}
}
