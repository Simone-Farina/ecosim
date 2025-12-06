package domain

import cats._
import cats.data.StateT
import cats.implicits._
import monix.newtypes._

import java.util.UUID

object Newtypes {
  type FirmId = FirmId.Type

  object FirmId extends NewtypeWrapped[UUID]

  type Quantity = Quantity.Type

  object Quantity extends NewtypeValidated[Int] {
    override def apply(value: Int): Either[BuildFailure[Type], Type] =
      if (value < 0) Left(BuildFailure("Quantity cannot be negative"))
      else Right(unsafeCoerce(value))

    implicit final class QuantityOps(private val self: Type) extends AnyVal {
      def +(other: Type): Either[BuildFailure[Type], Type] = Quantity.apply(self.value + other.value)
    }
  }

  type Money = Money.Type

  object Money extends NewtypeWrapped[BigDecimal] {
    implicit final class MoneyOps(private val self: Type) extends AnyVal {
      def +(other: Type): Type = Money(self.value + other.value)

      def -(other: Type): Type = Money(self.value - other.value)

      def <(other: Type): Boolean = self.value < other.value

      def >(other: Type): Boolean = self.value > other.value

      def >=(other: Type): Boolean = self.value >= other.value
    }
  }

  type Failable[A] = Either[String, A]
  type Sim[A] = StateT[Failable, Firm, A]
}
