package domain.models

import cats.data.ValidatedNel
import cats.syntax.all._
import domain.Newtypes._
import domain.models.Firm.FirmOp
import io.circe.Codec
import io.circe.generic.semiauto._
import sttp.tapir.Schema

final case class Firm(
    id: FirmId,
    cash: Money,
    quantity: Quantity,
    price: Money,
    debt: Money,
    tech: Double,
    employees: Int,
    wage: Money
) {
  lazy val netCapital: Money = cash - debt

  private def validateBudget(b: Money): ValidatedNel[String, Money] =
    if (b < Money(0)) "Negative budget".invalidNel else b.validNel

  private def validateFunds(firm: Firm, b: Money): ValidatedNel[String, Money] =
    if (firm.cash < b) "Insufficient funds".invalidNel else b.validNel

  def produce(budget: Money): FirmOp[String] = {
    (validateBudget(budget), validateFunds(this, budget)).tupled.toEither
      .leftMap(_.mkString_(" & "))
      .flatMap { case (validBudget, _) =>
        val calculatedQuantity =
          this.quantity.value + (validBudget.value * tech)
        val maxCapacity = (employees * tech * 10).toInt

        Quantity(math.min(calculatedQuantity.intValue, maxCapacity))
          .leftMap(_.toReadableString)
          .map { newQuantity =>
            this.copy(
              cash = cash - budget,
              quantity = newQuantity
            )
          }
      }
  }

  def investInRnD(budget: Money): FirmOp[String] = {
    validateFunds(this, budget).map {
      case validatedBudget @ _ if validatedBudget < Money(1000) =>
        this.copy(cash = cash - budget)
      case _ => this.copy(cash = cash - budget, tech = tech + 0.1)
    }
  }.toEither.leftMap(_.mkString_(" & "))

  def payWages: FirmOp[String] =
    validateFunds(this, Money(this.employees * this.wage.value))
      .map { wages =>
        this.copy(cash = cash - wages)
      }
      .toEither
      .leftMap(_.mkString_(" & "))
}

object Firm {
  type FirmOp[E] = Either[E, Firm]
}
