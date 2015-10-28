logLevel := Level.Warn

resolvers ++= Seq(
  "Restlet Repository" at "http://maven.restlet.org",
  "Typesafe" at "http://repo.typesafe.com/typesafe/releases/"
)

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.7")