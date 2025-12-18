package domain.models

import domain.Newtypes.Money._
import domain.Newtypes._

final case class Household(
    id: HouseholdId,
    cash: Money,
    income: Money,
    mpc: Double,
    employer: Option[FirmId]
) {
  def planBudget: Money = {
    val desiredSpending = Money(income.value * mpc)
    val maxAffordable = cash + income

    if (desiredSpending > maxAffordable) maxAffordable else desiredSpending
  }
}
