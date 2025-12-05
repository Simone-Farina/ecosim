package domain

import cats.data.ValidatedNel
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

  private def validateBudget(b: Money): ValidatedNel[String, Money] =
    if (b < Money(0)) "Negative budget".invalidNel else b.validNel

  private def validateCash(firm: Firm, b: Money): ValidatedNel[String, Money] =
    if (firm.cash < b) "Insufficient funds".invalidNel else b.validNel

  private def validateInvestment(b: Money): ValidatedNel[String, Money] =
    if (b < Money(1000)) "Insufficient resources for tech improvement".invalidNel else b.validNel

  def produce(budget: Money): Either[String, Firm] = {
    (validateBudget(budget), validateCash(this, budget)).mapN {
      case (budget, _) => this.copy(
        cash = this.cash - budget,
        quantity = Quantity.unsafe(this.quantity.value + (budget.value * tech).toInt)
      )
    }
  }.toEither.leftMap(_.mkString_(" & "))

  def investInRnD(budget: Money): Either[String, Firm] =
    validateInvestment(budget)
      .map(_ => this.copy(tech = tech + 0.1))
      .toEither
      .leftMap(_.mkString_(" & "))
}
