package com.azavea.busplan.routing

import org.opentripplanner.graph_builder.module.osm.DefaultWayPropertySetSource
import org.opentripplanner.graph_builder.module.osm.WayPropertySet
import org.opentripplanner.graph_builder.module.osm.WayPropertySetSource
import org.opentripplanner.routing.edgetype.StreetTraversalPermission

class BusPlanWayPropertySet() extends WayPropertySetSource {

	/* (non-Javadoc)
	 * @see org.opentripplanner.graph_builder.module.osm.WayPropertySetSource#populateProperties(org.opentripplanner.graph_builder.module.osm.WayPropertySet)
	 */
  override
  def populateProperties(props: WayPropertySet): Unit = {
    // Restrict cars from traversing limited access highways
    props.setProperties(
      "highway=motorway",
      StreetTraversalPermission.NONE,
      2.06,
      2.06
    )

    // Read the rest of permissions from default way property set
    new DefaultWayPropertySetSource().populateProperties(props)
  }
}
