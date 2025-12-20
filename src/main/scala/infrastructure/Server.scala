package infrastructure

import api.endpoints.SimulationEndpoints
import cats.effect.IO
import cats.syntax.all._
import com.comcast.ip4s._
import domain.SimulationState
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

object Server {

  def run(state: SimulationState): IO[Unit] = {

    val worldServerEndpoint = SimulationEndpoints.getWorldStatus
      .serverLogic(_ => state.worldRef.get.map(_.asRight[Unit]))

    val resumeEndpoint
        : ServerEndpoint.Full[Unit, Unit, Unit, Unit, String, Any, IO] =
      SimulationEndpoints.resumeEndpoint
        .serverLogic[IO](_ =>
          state.pausedRef.set(false).as(Right("Simulation Resumed"))
        )

    val pauseEndpoint
        : ServerEndpoint.Full[Unit, Unit, Unit, Unit, String, Any, IO] =
      SimulationEndpoints.pauseEndpoint
        .serverLogic[IO](_ =>
          state.pausedRef.set(true).as(Right("Simulation Paused"))
        )

    val resetEndpoint
        : ServerEndpoint.Full[Unit, Unit, Unit, Unit, String, Any, IO] =
      SimulationEndpoints.resetEndpoint
        .serverLogic[IO](_ =>
          for {
            _ <- state.pausedRef.set(true)
            newWorld = domain.services.WorldGenerator.generate(5, 10)
            _ <- state.worldRef.set(newWorld)
            _ <- state.pausedRef.set(false)
          } yield Right("World Reset Successfully")
        )

    val swaggerEndpoints = SwaggerInterpreter().fromEndpoints[IO](
      List(SimulationEndpoints.getWorldStatus),
      "EcoSim API",
      "1.0"
    )

    val routes = Http4sServerInterpreter[IO]().toRoutes(
      List(
        worldServerEndpoint,
        resumeEndpoint,
        pauseEndpoint,
        resetEndpoint
      ) ++ swaggerEndpoints
    )

    val httpApp = Router[IO]("/" -> routes).orNotFound

    EmberServerBuilder
      .default[IO]
      .withHost(host"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(httpApp)
      .build
      .useForever
  }
}
