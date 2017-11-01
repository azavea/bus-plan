name := "open-trip-planner"

libraryDependencies ++= Seq(
  "org.opentripplanner" % "otp" % "1.2.0"
    exclude("org.onebusaway", "onebusaway-csv-entities"),
  "org.onebusaway" % "onebusaway-csv-entities" % "1.1.4"
)

resolvers ++= Seq(
  "geosolutions" at "http://maven.geo-solutions.it/",
  "osgeo" at "http://download.osgeo.org/webdav/geotools/",
  "java.net" at "http://download.java.net/maven/2/",
  "axis" at "https://people.apache.org/repo/m1-ibiblio-rsync-repository/org.apache.axis2/",
  "conveyal" at "https://maven.conveyal.com/"
)

fork in run := true

javaOptions += "-Xmx4G"
