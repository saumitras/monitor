import AssemblyKeys._

assemblySettings

mainClass in assembly := Some("play.core.server.NettyServer")

fullClasspath in assembly += Attributed.blank(PlayKeys.playPackageAssets.value)

name := "GBMonitor"

version := "1.0"

lazy val `gbmonitor` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

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
