package domain

import cats.syntax.all._
import domain.Newtypes._

case class Firm(
                 id: FirmId,
                 cash: Money,
                 quantity: Quantity,
                 debt: Money,
                 tech: Double
               ) {
  lazy val netCapital: Money = cash - debt

  def produce(budget: Money): Either[String, Firm] = {
    if (cash - budget < Money(0)) Left("Not enough cash to cover production costs")
    else Quantity((budget.value * tech).toInt)
      .map(q => this.copy(
        cash = cash - budget,
        quantity = q)
      )
      .leftMap(ex => ex.toReadableString)
  }
}
