package infrastructure

import cats.syntax.all._
import api.endpoints.SimulationEndpoints
import cats.effect.IO
import cats.effect.kernel.Ref
import com.comcast.ip4s._
import domain.models.World
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

object Server {

  def run(worldRef: Ref[IO, World]): IO[Unit] = {

    val worldServerEndpoint = SimulationEndpoints.getWorldStatus
      .serverLogic(_ => worldRef.get.map(_.asRight[Unit]))

    val swaggerEndpoints = SwaggerInterpreter().fromEndpoints[IO](
      List(SimulationEndpoints.getWorldStatus),
      "EcoSim API",
      "1.0"
    )

    val routes = Http4sServerInterpreter[IO]().toRoutes(
      List(worldServerEndpoint) ++ swaggerEndpoints
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
