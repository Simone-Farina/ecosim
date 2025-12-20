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
val flywayVersion = "10.7.0"   // Passiamo alla v10 stabile
val logbackVersion = "1.5.22"  // Per i log

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-ember-server" % http4sVersion,
  "org.http4s" %% "http4s-ember-client" % http4sVersion,
  "org.http4s" %% "http4s-circe"        % http4sVersion,
  "org.http4s" %% "http4s-dsl"          % http4sVersion,

  "io.monix"   %% "newtypes-core"       % "0.3.0",

  "io.circe"   %% "circe-generic"       % circeVersion,
  "io.circe"   %% "circe-parser"        % circeVersion,

  "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe"    % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion,

  // --- PERSISTENZA ---
  "org.tpolecat" %% "doobie-core"      % doobieVersion,
  "org.tpolecat" %% "doobie-postgres"  % doobieVersion,
  "org.tpolecat" %% "doobie-hikari"    % doobieVersion,

  // --- MIGRAZIONI (Flyway 10 Setup) ---
  "org.flywaydb" % "flyway-core"                % flywayVersion,
  "org.flywaydb" % "flyway-database-postgresql" % flywayVersion, // NECESSARIO per Flyway 10+
  "org.postgresql" % "postgresql"               % "42.7.8",      // Driver Postgres aggiornato

  // --- LOGGING (Risolve SLF4J warning) ---
  "ch.qos.logback" % "logback-classic" % logbackVersion
)

addCompilerPlugin(
  "org.typelevel" %% "kind-projector" % "0.13.4" cross CrossVersion.full
)
scalacOptions += "-Ymacro-annotations"
scalacOptions += "-Wnonunit-statement"
