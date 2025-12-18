package infrastructure.codecs

import domain.models.{Firm, Household, World}
import io.circe.Codec
import io.circe.generic.semiauto._
import sttp.tapir.Schema
import sttp.tapir.generic.auto._

object SimulationCodecs extends NewtypeCodecs with NewtypeSchemas {
  implicit val firmCodec: Codec[Firm] = deriveCodec[Firm]
  implicit val householdCodec: Codec[Household] = deriveCodec[Household]
  implicit val worldCodec: Codec[World] = deriveCodec[World]

  implicit val firmSchema: Schema[Firm] = Schema.derived[Firm]
  implicit val householdSchema: Schema[Household] = Schema.derived[Household]
  implicit val worldSchema: Schema[World] = Schema.derived[World]
}