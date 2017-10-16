name := "optaplanner"

libraryDependencies ++= Seq(
  "com.graphhopper" % "jsprit-analysis"  % "1.7.2",
  "com.graphhopper" % "jsprit-core"  % "1.7.2",
  "com.graphhopper" % "jsprit-io"  % "1.7.2"
)

fork in Test := false
parallelExecution in Test := false
