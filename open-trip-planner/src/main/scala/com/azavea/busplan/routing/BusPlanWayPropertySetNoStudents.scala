package com.azavea.busplan.routing

import org.opentripplanner.graph_builder.module.osm.DefaultWayPropertySetSource
import org.opentripplanner.graph_builder.module.osm.WayPropertySet
import org.opentripplanner.graph_builder.module.osm.WayPropertySetSource
import org.opentripplanner.routing.edgetype.StreetTraversalPermission

class BusPlanWayPropertySetNoStudents() extends WayPropertySetSource {

  /* (non-Javadoc)
	 * @see org.opentripplanner.graph_builder.module.osm.WayPropertySetSource#populateProperties(org.opentripplanner.graph_builder.module.osm.WayPropertySet)
	 */
  override def populateProperties(props: WayPropertySet): Unit = {

    // Reduce speeds for each road type by 15%
    // Speeds ammended from source: https://github.com/opentripplanner/OpenTripPlanner/blob/bb29688dcee3e17032c57c21e81bb68362028b93/src/main/java/org/opentripplanner/graph_builder/module/osm/DefaultWayPropertySetSource.java
    // org.opentripplanner.graph_builder.module.osm.DefaultWayPropertySetSource.java
    props.setCarSpeed("highway=motorway", 24.7f); // 24.7 m/s ~= (0.85 * 65 mph)
    props.setCarSpeed("highway=motorway_link", 13.3f); // ~= 35 mph
    props.setCarSpeed("highway=trunk", 20.9f); // ~= 55 mph
    props.setCarSpeed("highway=trunk_link", 13.3f); // ~= 35 mph
    props.setCarSpeed("highway=primary", 17.1f); // ~= 45 mph
    props.setCarSpeed("highway=primary_link", 9.5f); // ~= 25 mph
    props.setCarSpeed("highway=secondary", 13.3f); // ~= 35 mph
    props.setCarSpeed("highway=secondary_link", 9.5f); // ~= 25 mph
    props.setCarSpeed("highway=tertiary", 9.5f); // ~= 25 mph
    props.setCarSpeed("highway=tertiary_link", 9.5f); // ~= 25 mph
    props.setCarSpeed("highway=living_street", 1.9f); // ~= 5 mph
    props.setCarSpeed("highway=pedestrian", 1.9f); // ~= 5 mph
    props.setCarSpeed("highway=residential", 9.5f); // ~= 25 mph
    props.setCarSpeed("highway=unclassified", 9.5f); // ~= 25 mph
    props.setCarSpeed("highway=service", 5.7f); // ~= 15 mph
    props.setCarSpeed("highway=track", 3.6f); // ~= 10 mph
    props.setCarSpeed("highway=road", 9.5f); // ~= 25 mph

    // default ~= 25 mph
    props.defaultSpeed = 9.5f;

    // Read the rest of permissions from default way property set
    new DefaultWayPropertySetSource().populateProperties(props)
  }
}
