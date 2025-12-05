import domain.Firm
import domain.Newtypes.{FirmId, Money, Quantity}

import java.util.UUID

object Main {
  def main(args: Array[String]): Unit = {
    val f = Firm(
      id = FirmId(UUID.randomUUID()),
      cash = Money(2000),
      quantity = Quantity(120).getOrElse(Quantity.unsafe(0)),
      debt = Money(100),
      tech = 0.8
    )
    val f1 = f.produce(Money(f.cash.value * 0.10)).flatMap(_.investInRnD(Money(1000)))
    f1.map(f => println(f.quantity, f.cash, f.tech))
  }
}