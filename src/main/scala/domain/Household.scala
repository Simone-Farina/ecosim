package domain

import domain.Newtypes._
import domain.Newtypes.Money._

case class Household(
                      id: HouseholdId,
                      cash: Money,
                      income: Money,
                      mpc: Double
                    ) {
  private def validateExpense(expense: Money): Money =
    if (expense > (cash + income)) cash + income
    else expense

  def decideSpending: Money = validateExpense(Money(income.value * mpc))
}
