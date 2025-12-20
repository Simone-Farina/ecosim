package infrastructure.persistence

import cats.effect.IO
import domain.models.Firm
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor
import infrastructure.persistence.dto.FirmSnapshot
import infrastructure.persistence.Metas._

sealed trait SnapshotRepository {
  def saveFirms(step: Int, firms: List[Firm]): IO[Unit]
}

class PostgresSnapshotRepository(xa: Transactor[IO])
    extends SnapshotRepository {

  private object SQL {
    val insert: Update[FirmSnapshot] = Update[FirmSnapshot](
      """INSERT INTO firm_snapshots (
        | step_number, firm_id, cash, inventory, price,
        | production, sales, employees, wage, tech_factor
        |) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""".stripMargin
    )
  }

  override def saveFirms(step: Int, firms: List[Firm]): IO[Unit] = {
    val snapshots = firms.map(f => FirmSnapshot.fromDomain(step, f))

    SQL.insert
      .updateMany(snapshots)
      .transact(xa)
      .void
  }
}
