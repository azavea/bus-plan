name := "testProblem"

javacOptions ++= Seq("-Xlint:deprecation", "-Xlint:unchecked")

libraryDependencies ++= Seq(
  "org.drools"      % "drools-compiler"  % "7.3.0.Final",
  "org.drools"      % "drools-core"      % "7.3.0.Final",
  "org.kie"         % "kie-api"          % "7.3.0.Final",
  "org.kie"         % "kie-internal"     % "7.3.0.Final",
  "org.optaplanner" % "optaplanner-core" % "7.3.0.Final",
  "org.optaplanner" % "optaplanner-bom"  % "7.3.0.Final"
)

fork in Test := false
parallelExecution in Test := false
