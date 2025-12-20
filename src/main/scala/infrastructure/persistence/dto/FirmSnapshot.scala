package infrastructure.persistence.dto

import domain.Newtypes.{FirmId, Money, Quantity}
import domain.models.Firm

final case class FirmSnapshot(
    step: Int,
    firmId: FirmId,
    cash: Money,
    inventory: Quantity,
    price: Money,
    production: Quantity,
    sales: Quantity,
    employees: Int,
    wage: Money,
    tech: Double
)

object FirmSnapshot {
  def fromDomain(step: Int, f: Firm): FirmSnapshot =
    FirmSnapshot(
      step = step,
      firmId = f.id,
      cash = f.cash,
      inventory = f.quantity,
      price = f.price,
      production = Quantity.unsafe(0),
      sales = Quantity.unsafe(0),
      employees = f.employees,
      wage = f.wage,
      tech = f.tech
    )
}
