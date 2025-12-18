import cats.effect.{IO, IOApp}
import domain.Newtypes.{FirmId, HouseholdId, Money, Quantity}
import domain.models.{Firm, Household, World}
import domain.services.WorldEngine

import java.util.UUID

object Main extends IOApp.Simple {

  override def run: IO[Unit] = {
    val firms = Iterator.fill(5)(Firm(
      id = FirmId(UUID.randomUUID()),
      cash = Money(2000),
      quantity = Quantity(120).getOrElse(Quantity.unsafe(0)),
      price = Money(10),
      debt = Money(100),
      tech = 0.8,
      employees = 2,
      wage = Money(100)
    )).toList

    val households = Iterator.fill(10)(Household(
      id = HouseholdId(UUID.randomUUID()),
      cash = Money(32000),
      income = Money(0),
      mpc = 0.8,
      employer = None
    )).toList

    val world = World(
      firms = firms,
      households = firms
        .flatMap(firm => Iterator.fill(firm.employees)(firm.id).toList)
        .zip(households)
        .map {
          case (firm, household) => household.copy(employer = Some(firm))
        }
    )

    WorldEngine.nextStep(world).map(println)
  }.void
}