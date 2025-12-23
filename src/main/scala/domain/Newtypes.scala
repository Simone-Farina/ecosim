package domain

import cats.data.StateT
import cats.effect.IO
import domain.models.Firm
import monix.newtypes._

import java.util.UUID

object Newtypes {
  type FirmId = FirmId.Type

  object FirmId extends NewtypeWrapped[UUID]

  type HouseholdId = HouseholdId.Type

  object HouseholdId extends NewtypeWrapped[UUID]

  type Quantity = Quantity.Type

  object Quantity extends NewtypeValidated[Int] {
    override def apply(value: Int): Either[BuildFailure[Type], Type] =
      if (value < 0) Left(BuildFailure("Quantity cannot be negative"))
      else Right(unsafeCoerce(value))

    implicit final class QuantityOps(private val self: Type) extends AnyVal {
      def +(other: Type): Either[BuildFailure[Type], Type] =
        Quantity.apply(self.value + other.value)

      def -(other: Type): Either[BuildFailure[Type], Type] =
        Quantity.apply(self.value - other.value)

      def >(other: Type): Boolean = self.value > other.value
      def <(other: Type): Boolean = self.value < other.value
    }
  }

  type Money = Money.Type

  object Money extends NewtypeWrapped[BigDecimal] {
    implicit final class MoneyOps(private val self: Money) extends AnyVal {
      def +(other: Money): Money = Money(self.value + other.value)

      def -(other: Money): Money = Money(self.value - other.value)

      def /(other: Money): BigDecimal = self.value / other.value

      def <(other: Money): Boolean = self.value < other.value

      def >(other: Money): Boolean = self.value > other.value

      def >=(other: Money): Boolean = self.value >= other.value
    }
  }

  type Failable[A] = Either[String, A]
  type Sim[A] = StateT[Failable, Firm, A]
  type BudgetOp[A] = StateT[IO, Money, A]

  object BudgetOp {
    def apply[A](f: Money => IO[(Money, A)]): BudgetOp[A] = StateT(f)

    def unit: BudgetOp[Unit] = StateT.pure(())
  }
}
