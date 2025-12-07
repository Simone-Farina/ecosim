package domain

import domain.Newtypes._

case class Household(
                      id: HouseholdId,
                      cash: Money,
                        income: Money,
                      mpc: Double
                    ) {
  private def validateExpense(expense: Money): Money =
    if (expense.value > (cash.value + income.value)) Money(cash.value + income.value)
    else expense

  def decideSpending: Money = validateExpense(Money(income.value + mpc))
}
