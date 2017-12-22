import Dependencies._

lazy val Versions = new {
  val akka = "2.5.8"
}

lazy val TcpStreamsServer = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "darkDesire",
      scalaVersion := "2.12.3",
      version      := "0.0.1"
    )),
    name := "TcpStreamsServer",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      "com.typesafe.akka" %% "akka-actor" % Versions.akka,
      "com.typesafe.akka" %% "akka-testkit" % Versions.akka % Test,
      "com.typesafe.akka" %% "akka-stream" % Versions.akka,
      "com.typesafe.akka" %% "akka-stream-testkit" % Versions.akka % Test
    )
  )
