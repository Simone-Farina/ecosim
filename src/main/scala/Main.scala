import cats.effect._
import cats.implicits._
import domain.SimulationState
import domain.models.World
import domain.services.{WorldEngine, WorldGenerator}
import infrastructure.Server
import infrastructure.persistence.{DatabaseConfig, PostgresSnapshotRepository}

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    DatabaseConfig.makeTransactor.use { xa =>
      for {
        _ <- IO.println("🚀 Database Initialization..")
        _ <- DatabaseConfig.initializeDb(xa)
        _ <- IO.println("✅ Database ready.")

        repository = new PostgresSnapshotRepository(xa)
        initialWorld = WorldGenerator.generate(5, 10)
        state <- SimulationState.make(initialWorld)

        persistHook = (step: Int, world: World) =>
          for {
            _ <- IO.println(s"💾 Persisting step $step...")
            _ <- repository.saveFirms(step, world.firms)
          } yield ()

        _ <- (
          Server.run(state),
          WorldEngine.simulationLoop(
            state.worldRef,
            state.pausedRef,
            persistHook
          )
        ).parTupled

      } yield ExitCode.Success
    }
  }
}
