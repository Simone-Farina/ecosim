package infrastructure.persistence

import doobie.postgres.implicits._
import domain.Newtypes.{FirmId, HouseholdId, Money, Quantity}
import doobie.Meta
import infrastructure.persistence.dto.FirmSnapshot

object Metas {
  implicit val moneyMeta: Meta[Money] =
    Meta[BigDecimal].timap(Money.apply)(_.value)
  implicit val quantityMeta: Meta[Quantity] =
    Meta[Int].timap(Quantity.unsafe)(_.value)
  implicit val firmIdMeta: Meta[FirmId] =
    Meta[java.util.UUID].timap(FirmId.apply)(_.value)
  implicit val householdIdMeta: Meta[HouseholdId] = {
    Meta[java.util.UUID].timap(HouseholdId.apply)(_.value)
  }
}
