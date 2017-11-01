/**
 * 
 */
package sdpBusRoutingCostFunction;

import java.io.IOException;
import java.util.LinkedList;

/**
 * @author azavea
 *
 */
public class routeFullCsv {
	/**
	 * @param args
	 */
	/**
	 * @param cost
	 * @param inCsv
	 * @param outCsv
	 * @param students
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static void routeCsv(costFunction cost, String inCsv, csvWriter outCsv, boolean students)
			throws ClassNotFoundException, IOException {
		LinkedList<sdpNode> allRoutes = csvReader.readCsv(inCsv);
		long currentTime = 0;
		for (int i = 0; i < allRoutes.size(); i++) {
			sdpNode n = allRoutes.get(i);
			if (n.sequence == 0) {
				currentTime = n.time;
			}
			cost.routeVehicle(students, currentTime, n.start, n.end);
			cost.writeRouteToCsv(outCsv, n.route, n.sequence);
		}
	}
}
