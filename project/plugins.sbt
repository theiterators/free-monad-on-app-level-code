logLevel := Level.Warn

addSbtPlugin("com.geirsson" %% "sbt-scalafmt" % "0.4.10")

resolvers += "Flyway" at "https://flywaydb.org/repo"
addSbtPlugin("org.flywaydb" % "flyway-sbt" % "4.0.1")

addSbtPlugin("io.spray" % "sbt-revolver" % "0.8.0")
