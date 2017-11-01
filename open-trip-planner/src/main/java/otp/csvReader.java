/**
 * 
 */
package sdpBusRoutingCostFunction;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 * @author azavea
 *
 */
public class csvReader {

	/**
	 * @param csvFile
	 * @return
	 * @throws IOException
	 */
	public static LinkedList<sdpNode> readCsv(String csvFile) throws IOException {

		Reader in = new FileReader(csvFile);

		LinkedList<sdpNode> allStops = new LinkedList<sdpNode>();

		int i = 0;

		Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(in);
		for (CSVRecord record : records) {

			String xStart = record.get("x1");
			String yStart = record.get("y1");
			String xEnd = record.get("x2");
			String yEnd = record.get("y2");
			String route = record.get("route");
			String time = record.get("pickup");
			int sequence = i;

			i++;

			sdpNode node = new sdpNode(xStart, yStart, xEnd, yEnd, time, route, sequence);
			allStops.add(node);
		}

		return allStops;
	}

}
