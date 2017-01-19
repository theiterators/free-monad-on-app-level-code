name := "WombatCuddlerJobApp"
version := "1.0"
organization := "com.theiterators"
scalaVersion := "2.11.8"
scalacOptions := Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-Xlint:_",
  "-Xfatal-warnings",
  "-encoding",
  "utf8"
)

//debugging
Revolver.enableDebugging(port = 5005, suspend = false)
mainClass in reStart := Some("com.theiterators.wombatcuddler.main.Main")

flywayUrl := "jdbc:h2:./wombatcuddlers"

//integration tests
configs(IntegrationTest)
Defaults.itSettings
inConfig(IntegrationTest)(
  ScalaFmtPlugin.configScalafmtSettings ++ FlywayPlugin.flywayBaseSettings(IntegrationTest) ++ Seq(
    flywayUrl := "jdbc:h2:./wombatcuddlers-it",
    executeTests <<= executeTests dependsOn flywayMigrate,
    flywayMigrate <<= flywayMigrate dependsOn flywayClean))

scalafmtConfig := Some(file(".scalafmt.conf"))
reformatOnCompileWithItSettings

libraryDependencies ++= {
  val akkaV         = "2.4.8"
  val akkaPlayJsonV = "1.7.0"
  val catsV         = "0.9.0"
  val h2V           = "1.4.192"
  val scalaTestV    = "2.2.6"
  val slickV        = "3.1.1"
  val slf4jV        = "1.7.21"
  Seq(
    "com.typesafe.akka"  %% "akka-slf4j"             % akkaV,
    "com.typesafe.akka"  %% "akka-http-core"         % akkaV,
    "com.typesafe.akka"  %% "akka-http-experimental" % akkaV,
    "de.heikoseeberger"  %% "akka-http-play-json"    % akkaPlayJsonV,
    "org.typelevel"      %% "cats"                   % catsV,
    "com.typesafe.slick" %% "slick"                  % slickV,
    "com.typesafe.slick" %% "slick-hikaricp"         % slickV,
    "org.slf4j"          % "slf4j-nop"               % slf4jV,
    "com.h2database"     % "h2"                      % h2V,
    "org.scalatest"      %% "scalatest"              % scalaTestV % "test,it",
    "com.typesafe.akka"  %% "akka-http-testkit"      % akkaV % "it"
  )
}
addCompilerPlugin("com.milessabin" % "si2712fix-plugin" % "1.2.0" cross CrossVersion.full)
addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.8.0")
