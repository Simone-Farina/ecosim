package infrastructure.persistence

import cats.effect.IO
import cats.effect.kernel.Resource
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import org.flywaydb.core.Flyway

object DatabaseConfig {

  def makeTransactor: Resource[IO, HikariTransactor[IO]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[IO](32)
      xa <- HikariTransactor.newHikariTransactor[IO](
        "org.postgresql.Driver",
        "jdbc:postgresql://localhost:5432/ecosim_db",
        "ecosim",
        "password",
        ce
      )
    } yield xa

  def initializeDb(transactor: HikariTransactor[IO]): IO[Unit] =
    transactor.configure { datasource =>
      IO {
        Flyway
          .configure()
          .dataSource(datasource)
          .load()
          .migrate()
      }.void
    }
}
