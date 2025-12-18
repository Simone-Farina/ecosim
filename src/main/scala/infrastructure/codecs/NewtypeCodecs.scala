package infrastructure.codecs

import domain.Newtypes.{FirmId, HouseholdId, Money, Quantity}
import io.circe.{Decoder, Encoder}

import java.util.UUID

trait NewtypeCodecs {
  implicit val moneyEncoder: Encoder[Money] =
    Encoder[BigDecimal].contramap(_.value)
  implicit val moneyDecoder: Decoder[Money] =
    Decoder[BigDecimal].map(Money.apply)

  implicit val quantityEncoder: Encoder[Quantity] =
    Encoder[Int].contramap(_.value)
  implicit val quantityDecoder: Decoder[Quantity] =
    Decoder[Int].map(Quantity.unsafe)

  implicit val firmIdEncoder: Encoder[FirmId] =
    Encoder[UUID].contramap(_.value)
  implicit val firmIdDecoder: Decoder[FirmId] =
    Decoder[UUID].map(FirmId.apply)

  implicit val householdIdEncoder: Encoder[HouseholdId] =
    Encoder[UUID].contramap(_.value)
  implicit val householdIdDecoder: Decoder[HouseholdId] =
    Decoder[UUID].map(HouseholdId.apply)
}
