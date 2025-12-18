ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.18"

val http4sVersion = "0.23.33"
val circeVersion = "0.14.15"
val tapirVersion = "1.13.3"

lazy val root = (project in file("."))
  .settings(
    name := "ecosim",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.13.0",
      "org.typelevel" %% "cats-effect" % "3.6.3",
      "org.scalameta" %% "munit" % "1.2.1" % Test
    )
  )

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-ember-server" % http4sVersion,
  "org.http4s" %% "http4s-ember-client" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "io.monix" %% "newtypes-core" % "0.3.0",
  "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion
)
addCompilerPlugin(
  "org.typelevel" %% "kind-projector" % "0.13.4" cross CrossVersion.full
)
scalacOptions += "-Ymacro-annotations"
scalacOptions += "-Wnonunit-statement"
