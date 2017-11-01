/**
 * 
 */
package sdpBusRoutingCostFunction;

import java.io.*;

/**
 * @author azavea
 *
 */
public class csvWriter {
	private String file;

	// TODO: overload constructor to make CSV if it doesn't already exist
	/**
	 * @param existingFile
	 */
	public csvWriter(String existingFile) {
		this.file = existingFile;
	}

	/**
	 * @param startLon
	 * @param startLat
	 * @param endLon
	 * @param endLat
	 * @param startTime
	 * @param endTime
	 * @param Route
	 * @param stopSequence
	 * @param stateSequence
	 */
	public void appendRow(double startLon, double startLat, double endLon, double endLat, long startTime, long endTime,
			String Route, int stopSequence, int stateSequence) {

		BufferedWriter bw = null;

		try {
			bw = new BufferedWriter(new FileWriter(this.file, true));
			// TODO this needs to be cleaner
			bw.write(startLon + "," + startLat + "," + endLon + "," + endLat + "," + startTime + "," + endTime + ","
					+ Route + "," + stopSequence + "," + stateSequence);
			bw.newLine();
			bw.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (bw != null)
				try {
					bw.close();
				} catch (IOException ioe2) {
				}
		}
	}
}
