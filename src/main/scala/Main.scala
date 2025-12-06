import domain.Firm
import domain.Newtypes.{FirmId, Money, Quantity, Sim}
import domain.SimulationOps.{investS, produceS}

import java.util.UUID

object Main {
  def main(args: Array[String]): Unit = {
    val initialFirm = Firm(
      id = FirmId(UUID.randomUUID()),
      cash = Money(2000),
      quantity = Quantity(120).getOrElse(Quantity.unsafe(0)),
      debt = Money(100),
      tech = 0.8
    )

    val program: Sim[Unit] = for {
      _ <- produceS(Money(100))
      _ <- investS(Money(1000))
    } yield ()

    val result = program.run(initialFirm)
    println(result)
  }
}