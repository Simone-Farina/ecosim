ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.18"

lazy val root = (project in file("."))
  .settings(
    name := "ecosim",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.13.0",
      "org.typelevel" %% "cats-effect" % "3.6.3",
      "org.scalameta" %% "munit" % "1.2.1" % Test
    )
  )

val http4sVersion = "0.23.33"
val circeVersion = "0.14.15"
val tapirVersion = "1.13.3"
val doobieVersion = "1.0.0-RC11"
val flywayVersion = "10.7.0" // DO NOT UPDATE CURRENTLY UNSUPPORTED
val logbackVersion = "1.5.23"
val enumeratumVersion = "1.9.1"

libraryDependencies ++= Seq(
  // --- HTTP4S
  "org.http4s" %% "http4s-ember-server" % http4sVersion,
  "org.http4s" %% "http4s-ember-client" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "org.http4s" %% "http4s-dsl" % http4sVersion,

  // -- MONIX
  "io.monix" %% "newtypes-core" % "0.3.0",

  // --- CIRCE
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,

  // --- TAPIR
  "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion,

  // --- PERSISTENZA ---
  "org.tpolecat" %% "doobie-core" % doobieVersion,
  "org.tpolecat" %% "doobie-postgres" % doobieVersion,
  "org.tpolecat" %% "doobie-hikari" % doobieVersion,

  // --- MIGRAZIONI (Flyway 10 Setup) ---
  "org.flywaydb" % "flyway-core" % flywayVersion,
  "org.flywaydb" % "flyway-database-postgresql" % flywayVersion,
  "org.postgresql" % "postgresql" % "42.7.8",

  // --- LOGGING (Risolve SLF4J warning) ---
  "ch.qos.logback" % "logback-classic" % logbackVersion,

  // --- ENUMERATUM
  "com.beachape" %% "enumeratum" % enumeratumVersion,
  "com.beachape" %% "enumeratum-circe" % enumeratumVersion,
  "com.beachape" %% "enumeratum-doobie" % enumeratumVersion
)

addCompilerPlugin(
  "org.typelevel" %% "kind-projector" % "0.13.4" cross CrossVersion.full
)
scalacOptions += "-Ymacro-annotations"
scalacOptions += "-Wnonunit-statement"
