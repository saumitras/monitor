name := "GBMonitor"

version := "1.0"

lazy val `gbmonitor` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

resolvers += "Restlet Repository" at "http://maven.restlet.org"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-mailer" % "2.4.1" withSources(),
  "joda-time" % "joda-time" % "2.4"  withSources(),
  "org.apache.curator" % "curator-client" % "2.7.0"  withSources(),
  "org.apache.curator" % "curator-framework" % "2.7.0"  withSources(),
  "org.apache.curator" % "curator-recipes" % "2.7.0"  withSources(),
  "org.apache.solr" % "solr-core" % "5.2.1" excludeAll(ExclusionRule(organization = "org.slf4j"), ExclusionRule(organization = "com.google.protobuf")),
  "org.apache.solr" % "solr-solrj" % "5.2.1"  withSources(),
  "org.json4s" %% "json4s-native" % "3.2.11"  withSources(),
  "org.json4s" %% "json4s-jackson" % "3.2.11" withSources(),
  "commons-io" % "commons-io" % "2.4"  withSources(),
  "org.commonjava.googlecode.markdown4j" % "markdown4j" % "2.2-cj-1.0"  withSources(),
  //"com.typesafe.slick" %% "slick" % "3.0.2" withSources() withJavadoc(),
  "com.typesafe.slick" % "slick_2.11" % "2.1.0" withSources(),
  //"org.slf4j" % "slf4j-nop" % "1.6.4" withSources(),
  "com.h2database" % "h2" % "1.3.176" withSources(),
  "com.mchange" % "c3p0" % "0.9.5"  withSources() withJavadoc(),
  "com.typesafe.akka" % "akka-actor_2.11" % "2.3.9"  withSources() withJavadoc(),
  "com.typesafe.akka" %% "akka-remote" % "2.3.9"  withSources() withJavadoc(),
  "javax.mail" % "mail" % "1.4.7" withSources() withJavadoc()
)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

libraryDependencies ~= { _ map {
  case m if m.organization == "com.typesafe.play" =>
    m.exclude("commons-logging", "commons-logging")
  case m => m
}}

// Take the first ServerWithStop because it's packaged into two jars
assemblyMergeStrategy in assembly := {
  case "play/core/server/ServerWithStop.class" => MergeStrategy.first
  case "org/slf4j/impl/StaticLoggerBinder.class" => MergeStrategy.first
  case "org/slf4j/impl/StaticMarkerBinder.class" => MergeStrategy.first
  case "org/slf4j/impl/StaticMDCBinder.class" => MergeStrategy.first
  case other => (assemblyMergeStrategy in assembly).value(other)
}
