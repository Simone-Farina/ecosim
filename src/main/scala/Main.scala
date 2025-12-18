import cats.effect.kernel.Ref
import cats.effect.{IO, IOApp}
import domain.models.World
import domain.services.WorldGenerator

object Main extends IOApp.Simple {

  override def run: IO[Unit] = {
    val initialWorld = WorldGenerator.generate(5, 10)

    for {
      worldRef <- Ref.of[IO, World](initialWorld)
      _ <- (
        infrastructure.Server.run(worldRef),
        domain.services.WorldEngine.simulationLoop(worldRef)
      ).parTupled.void
    } yield ()
  }.void
}
