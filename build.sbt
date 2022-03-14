ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    organization := "uk.softar",
    name := "property-partners-task-scala",
    version := "0.1.0-SNAPSHOT",

    libraryDependencies ++= Seq(
      "org.scalaz" %% "scalaz-core" % "7.3.6"
    )
  )
