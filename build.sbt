name := "Airbrake Errors"

scalaVersion := "2.10.0"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "net.databinder.dispatch" %% "dispatch-core" % "0.10.1",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "joda-time"         % "joda-time"           % "2.1",
  "org.joda" % "joda-convert" % "1.2",
  "com.typesafe" % "config" % "0.4.0",
  "com.typesafe.akka" %% "akka-actor" % "2.2-M3"
)

Seq(
  scalaSource in Compile <<= baseDirectory / "src",
  javaSource in Compile <<= baseDirectory / "src",
  sourceDirectory in Compile <<= baseDirectory / "src",
  scalaSource in Test <<= baseDirectory / "test",
  javaSource in Test <<= baseDirectory / "test",
  sourceDirectory in Test <<= baseDirectory / "test",
  resourceDirectory in Compile <<= baseDirectory / "conf"
)
