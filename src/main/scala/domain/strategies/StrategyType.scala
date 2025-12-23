package domain.strategies

import enumeratum._
import sttp.tapir.Schema

sealed trait StrategyType extends EnumEntry

object StrategyType
    extends Enum[StrategyType]
    with CirceEnum[StrategyType]
    with DoobieEnum[StrategyType] {
  case object Balanced extends StrategyType
  case object Aggressive extends StrategyType
  case object Conservative extends StrategyType

  override def values: IndexedSeq[StrategyType] = findValues
  implicit val schema: Schema[StrategyType] = Schema.derived
}
