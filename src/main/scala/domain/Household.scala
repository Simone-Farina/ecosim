package domain

import domain.Newtypes._
import domain.Newtypes.Money._

case class Household(
    id: HouseholdId,
    cash: Money,
    income: Money,
    mpc: Double,
) {
  def planBudget: Money = {
    val desiredSpending = Money(income.value * mpc)
    val maxAffordable   = cash + income

    if (desiredSpending > maxAffordable) maxAffordable else desiredSpending
  }
}
