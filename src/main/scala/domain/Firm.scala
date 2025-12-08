package domain

import cats.data.ValidatedNel
import cats.syntax.all._
import domain.Newtypes._

case class Firm(
                 id: FirmId,
                 cash: Money,
                 quantity: Quantity,
                 price: Money,
                 debt: Money,
                 tech: Double
               ) {
  lazy val netCapital: Money = cash - debt

  private def validateBudget(b: Money): ValidatedNel[String, Money] =
    if (b < Money(0)) "Negative budget".invalidNel else b.validNel

  private def validateFunds(firm: Firm, b: Money): ValidatedNel[String, Money] =
    if (firm.cash < b) "Insufficient funds".invalidNel else b.validNel

  def produce(budget: Money): Either[String, Firm] = {
    (validateBudget(budget), validateFunds(this, budget))
      .tupled
      .toEither
      .leftMap(_.mkString_(" & "))
      .flatMap {
        case (validBudget, _) =>
          val calculatedQuantity = this.quantity.value + (validBudget.value * tech)

          Quantity(calculatedQuantity.intValue)
            .leftMap(_.toReadableString)
            .map { newQuantity =>
              this.copy(
                cash = cash - budget,
                quantity = newQuantity
              )
            }
      }
  }

  def investInRnD(budget: Money): Either[String, Firm] = {
    validateFunds(this, budget).map {
      case validatedBudget@_ if validatedBudget < Money(1000) => this.copy(cash = cash - budget)
      case _ => this.copy(cash = cash - budget, tech = tech + 0.1)
    }
  }.toEither.leftMap(_.mkString_(" & "))
}
