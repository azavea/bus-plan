/**
 * 
 */
package sdpBusRoutingCostFunction;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * @author azavea
 *
 */
public class sdpNode {
	Coordinate start;
	Coordinate end;
	long time;
	String route;
	int sequence;

	/**
	 * @param xStart
	 * @param yStart
	 * @param xEnd
	 * @param yEnd
	 * @param time
	 * @param route
	 * @param sequence
	 */
	@SuppressWarnings("null")
	public sdpNode(String xStart, String yStart, String xEnd, String yEnd, String time, String route, int sequence) {

		// Establish start and end coordinate objects
		this.start = new Coordinate(Double.parseDouble(xStart), Double.parseDouble(yStart));
		this.end = new Coordinate(Double.parseDouble(xEnd), Double.parseDouble(yEnd));

		this.sequence = sequence;
		this.route = route;

		Double timeDouble = Double.parseDouble(time);
		this.time = Math.round(timeDouble);

	}

}
