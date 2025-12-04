package domain

import domain.Newtypes._

case class Firm(
                 id: FirmId,
                 cash: Money,
                 quantity: Quantity,
                 debt: Money,
                 tech: Double
               ) {
  lazy val netCapital: BigDecimal = cash.value - debt.value
}
