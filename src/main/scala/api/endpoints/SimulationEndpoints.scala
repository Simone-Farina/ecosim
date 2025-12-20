package api.endpoints

import domain.models.{Firm, World}
import sttp.tapir._
import sttp.tapir.json.circe.jsonBody
import infrastructure.codecs.SimulationCodecs._

object SimulationEndpoints {
  private val baseEndpoint = endpoint.in("api" / "v1")

  val getFirmsEndpoint = baseEndpoint.get
    .in("firms")
    .out(jsonBody[List[Firm]])
    .description("Return a list of all firms")

  val getWorldStatus = baseEndpoint.get
    .in("world" / "status")
    .out(jsonBody[World])
    .description("Current state of the simulation")

  val pauseEndpoint = baseEndpoint.post
    .in("simulation" / "pause")
    .out(stringBody)
    .description("Pause the simulation")

  val resumeEndpoint = baseEndpoint.post
    .in("simulation" / "resume")
    .out(stringBody)
    .description("Resume the simulation")

  val resetEndpoint = baseEndpoint.post
    .in("simulation" / "reset")
    .out(stringBody)
    .description("Reset current world to initial state")
}
