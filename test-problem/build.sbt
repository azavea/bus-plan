name := "testProblem"

javacOptions ++= Seq("-Xlint:deprecation", "-Xlint:unchecked")

libraryDependencies ++= Seq(
  "com.google.guava"   % "guava"            % "20.0",
  "org.apache.commons" % "commons-csv"      % "1.5",
  "org.drools"         % "drools-compiler"  % "7.4.1.Final",
  "org.drools"         % "drools-core"      % "7.4.1.Final",
  "org.kie"            % "kie-api"          % "7.4.1.Final",
  "org.kie"            % "kie-internal"     % "7.4.1.Final",
  "org.optaplanner"    % "optaplanner-bom"  % "7.4.1.Final",
  "org.optaplanner"    % "optaplanner-core" % "7.4.1.Final"
)

fork in Test := false
parallelExecution in Test := false
