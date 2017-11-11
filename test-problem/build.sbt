name := "testProblem"

javacOptions ++= Seq("-Xlint:deprecation", "-Xlint:unchecked")

libraryDependencies ++= Seq(
  "org.drools"       % "drools-compiler"  % "7.4.1.Final",
  "org.drools"       % "drools-core"      % "7.4.1.Final",
  "org.kie"          % "kie-api"          % "7.4.1.Final",
  "org.kie"          % "kie-internal"     % "7.4.1.Final",
  "org.optaplanner"  % "optaplanner-core" % "7.4.1.Final",
  "org.optaplanner"  % "optaplanner-bom"  % "7.4.1.Final",
  "com.google.guava" % "guava"            % "20.0"
)

fork in Test := false
parallelExecution in Test := false
