/**
 * 
 */
package sdpBusRoutingCostFunction;

import java.util.LinkedList;
import java.util.List;

import org.opentripplanner.common.model.GenericLocation;
import org.opentripplanner.routing.core.RoutingRequest;
import org.opentripplanner.routing.core.State;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.graph.Vertex;
import org.opentripplanner.routing.impl.GraphPathFinder;
import org.opentripplanner.routing.spt.GraphPath;
import org.opentripplanner.standalone.Router;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * @author azavea
 *
 */
public class costFunction {

	Graph withStudents;
	Graph withoutStudents;
	GraphPath route;

	/**
	 * @param withStudents
	 * @param withoutStudents
	 */
	public costFunction(Graph withStudents, Graph withoutStudents) {
		this.withStudents = withStudents;
		this.withoutStudents = withoutStudents;
	}

	/**
	 * @param students
	 * @param eitherGraph
	 */
	public costFunction(boolean students, Graph eitherGraph) {
		if (students) {
			this.withStudents = eitherGraph;
		} else {
			this.withoutStudents = eitherGraph;
		}
	}

	// TODO: overload this method to take four ind x-y values rather than coord objects
	/**
	 * @param students
	 * @param time
	 * @param start
	 * @param end
	 */
	public void routeVehicle(boolean students, long time, Coordinate start, Coordinate end) {

		// Define routing request
		RoutingRequest routingRequest = new RoutingRequest("CAR");

		Router router;
		routingRequest.dateTime = Math.abs(time);
		routingRequest.from = new GenericLocation(start.x, start.y);
		routingRequest.to = new GenericLocation(end.x, end.y);

		if (students) {
			router = new Router("TEST", this.withStudents);
			routingRequest.setRoutingContext(this.withStudents);
		} else {
			router = new Router("TEST", this.withoutStudents);
			routingRequest.setRoutingContext(this.withoutStudents);
		}

		// TODO: Handle trivial path exception
		// Router is just a named graph object
		List<GraphPath> paths = new GraphPathFinder(router).getPaths(routingRequest);

		// Extract itinerary
		// TODO: should this return a route outside of the object?
		this.route = paths.get(0);
	}

	// TODO: add duration and computation time
	/**
	 * @return
	 */
	public int getCost() {
		return this.route.getDuration();
	}

	// TODO: documentation
	/**
	 * @param csv
	 * @param routeName
	 * @param sequence
	 */
	public void writeRouteToCsv(csvWriter csv, String routeName, int sequence) {
		LinkedList<State> allStates = this.route.states;

		long startTime = allStates.get(0).getStartTimeSeconds();
		for (int i = 1; i < allStates.size(); i++) {
			State startState = allStates.get(i - 1);
			State endState = allStates.get(i);
			Vertex coordsStart = startState.getVertex();
			Vertex coordsEnd = endState.getVertex();
			long timeChange = endState.getElapsedTimeSeconds();
			// This adjusts for a bug in otp source code
			long realEndTime = startTime + (timeChange * 1000);

			csv.appendRow(coordsStart.getX(), coordsStart.getY(), coordsEnd.getX(), coordsEnd.getY(), timeChange,
					realEndTime, routeName, sequence, i);
		}
	}

}
