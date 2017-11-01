/**
 * 
 */
package sdpBusRoutingCostFunction;

import org.opentripplanner.graph_builder.module.osm.DefaultWayPropertySetSource;
import org.opentripplanner.graph_builder.module.osm.WayPropertySet;
import org.opentripplanner.graph_builder.module.osm.WayPropertySetSource;
import org.opentripplanner.routing.edgetype.StreetTraversalPermission;

/**
 * @author azavea
 *
 */
public class sdpWayPropertySet implements WayPropertySetSource {

	/* (non-Javadoc)
	 * @see org.opentripplanner.graph_builder.module.osm.WayPropertySetSource#populateProperties(org.opentripplanner.graph_builder.module.osm.WayPropertySet)
	 */
	@Override
	public void populateProperties(WayPropertySet props) {
		// Restrict cars from traversing limited access highways
		props.setProperties("highway=motorway", StreetTraversalPermission.NONE, 2.06, 2.06);

		// Read the rest of permissions from default way property set
		new DefaultWayPropertySetSource().populateProperties(props);
	}

}
