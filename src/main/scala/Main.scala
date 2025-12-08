import cats.effect.{IO, IOApp}
import domain.Newtypes.{FirmId, HouseholdId, Money, Quantity}
import domain.{Firm, Household, World}
import service.WorldEngine

import java.util.UUID

object Main extends IOApp.Simple {

  override def run: IO[Unit] = {
    val firms = Iterator.fill(5)(Firm(
      id = FirmId(UUID.randomUUID()),
      cash = Money(2000),
      quantity = Quantity(120).getOrElse(Quantity.unsafe(0)),
      price = Money(10),
      debt = Money(100),
      tech = 0.8
    )).toList

    val households = Iterator.fill(10)(Household(
      id = HouseholdId(UUID.randomUUID()),
      cash = Money(32000),
      income = Money(2000),
      mpc = 0.8
    )).toList

    val world = World(
      firms = firms,
      households = households
    )
    WorldEngine.nextStep(world).map(println)
  }.void
}