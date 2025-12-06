import cats.effect.{IO, IOApp}
import domain.Newtypes.{FirmId, Money, Quantity}
import domain.{Firm, World}
import service.WorldEngine

import java.util.UUID

object Main extends IOApp.Simple {

  override def run: IO[Unit] = {
    val firm1 = Firm(
      id = FirmId(UUID.randomUUID()),
      cash = Money(2000),
      quantity = Quantity(120).getOrElse(Quantity.unsafe(0)),
      debt = Money(100),
      tech = 0.8
    )

    val firm2 = Firm(
      id = FirmId(UUID.randomUUID()),
      cash = Money(2000),
      quantity = Quantity(120).getOrElse(Quantity.unsafe(0)),
      debt = Money(100),
      tech = 0.8
    )

    val world = World(firm1 :: firm2 :: Nil)
    WorldEngine.nextStep(world).map(println)
  }.void
}