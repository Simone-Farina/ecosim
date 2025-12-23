package domain.strategies

import domain.Newtypes.{Money, Quantity}
import domain.models.Firm

object Strategies {

  trait FirmStrategy {
    def decide(firm: Firm): StrategyDecision
  }

  private val Balanced = new FirmStrategy {
    // Goal: Keep inventory between 10 and 50
    override def decide(firm: Firm): StrategyDecision = firm match {
      // warehouse full
      case Firm(_, _, quantity, _, _, _, _, _, _)
          if quantity > Quantity.unsafe(50) =>
        StrategyDecision(
          priceMultiplier = 0.9,
          productionQuota = 0.5,
          layoffs = 0
        )
      // warehouse emptying
      case Firm(_, _, quantity, _, _, _, _, _, _)
          if quantity < Quantity.unsafe(10) =>
        StrategyDecision(
          priceMultiplier = 1.1,
          productionQuota = 1.0,
          layoffs = 0
        )

      case Firm(_, cash, _, _, _, _, employees, wage, _) =>
        val salaries = employees * wage.value
        // low cash
        if (cash.value <= salaries)
          StrategyDecision(
            priceMultiplier = 1.0,
            productionQuota = 0.0,
            layoffs = 0
          )
        else
          StrategyDecision(
            priceMultiplier = 1.0,
            productionQuota = 0.85,
            layoffs = 0
          )
    }
  }

  private val Aggressive = new FirmStrategy {
    // Goal: Market Share
    override def decide(firm: Firm): StrategyDecision = firm match {
      case Firm(_, _, quantity, _, _, _, _, _, _)
          if quantity > Quantity.unsafe(20) =>
        StrategyDecision(
          priceMultiplier = 0.6,
          productionQuota = 0.7,
          layoffs = 0
        )
      case Firm(_, _, quantity, _, _, _, _, _, _)
          if quantity < Quantity.unsafe(8) =>
        StrategyDecision(
          priceMultiplier = 1.1,
          productionQuota = 1.0,
          layoffs = 0
        )
      case _ =>
        StrategyDecision(
          priceMultiplier = 1.0,
          productionQuota = 0.8,
          layoffs = 0
        )
    }
  }

  private val Conservative = new FirmStrategy {
    // --- Goal: Margin and Security
    override def decide(firm: Firm): StrategyDecision = firm match {
      case Firm(_, _, quantity, _, _, _, _, _, _)
          if quantity > Quantity.unsafe(30) =>
        StrategyDecision(
          priceMultiplier = 1.0,
          productionQuota = 0.0,
          layoffs = 0
        )
      case firm =>
        val wages = firm.wage.value * firm.employees
        if (Money(wages) > firm.cash)
          StrategyDecision(
            priceMultiplier = 1.0,
            productionQuota = 0.7,
            layoffs = -((Money(wages) - firm.cash) / Money(wages)).intValue
          )
        else
          StrategyDecision(
            priceMultiplier = 1.0,
            productionQuota = 0.6,
            layoffs = 0
          )
    }
  }

  def resolve(t: StrategyType): FirmStrategy = t match {
    case StrategyType.Balanced     => Balanced
    case StrategyType.Aggressive   => Aggressive
    case StrategyType.Conservative => Conservative
    case _                         => Balanced
  }
}
