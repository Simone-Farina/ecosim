package infrastructure.codecs

import domain.Newtypes._
import sttp.tapir.Schema

trait NewtypeSchemas {
  implicit val moneySchema: Schema[Money] =
    Schema.schemaForBigDecimal.map(m => Some(Money(m)))(_.value)
  implicit val quantitySchema: Schema[Quantity] =
    Schema.schemaForInt.map(q => Some(Quantity.unsafe(q)))(_.value)
  implicit val firmIdSchema: Schema[FirmId] =
    Schema.schemaForUUID.map(id => Some(FirmId(id)))(_.value)
  implicit val householdIdSchema: Schema[HouseholdId] =
    Schema.schemaForUUID.map(id => Some(HouseholdId(id)))(_.value)
}
