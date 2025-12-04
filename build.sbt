ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.18"

lazy val root = (project in file("."))
  .settings(
    name := "ecosim",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.13.0",
      "org.scalameta" %% "munit" % "1.2.1" % Test
    )
  )

libraryDependencies += "io.monix" %% "newtypes-core" % "0.3.0"

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.13.4" cross CrossVersion.full)
scalacOptions += "-Ymacro-annotations"