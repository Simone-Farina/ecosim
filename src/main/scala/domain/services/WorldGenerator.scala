package domain.services

import domain.Newtypes.{FirmId, HouseholdId, Money, Quantity}
import domain.models.{Firm, Household, World}
import domain.strategies.StrategyType.Balanced

import java.util.UUID

object WorldGenerator {
  def generate(numFirms: Int, numHouseholds: Int): World = {
    val firms = (1 to numFirms).toList.map { i =>
      Firm(
        id = FirmId(UUID.randomUUID()),
        cash = Money(50000),
        quantity = Quantity.unsafe(50),
        price = Money(20),
        debt = Money(0),
        tech = 50.0,
        employees = 2,
        wage = Money(1000),
        strategy = Balanced
      )
    }

    val households = (1 to numHouseholds).toList.map { i =>
      val employerId = firms(i % firms.length).id
      Household(
        id = HouseholdId(UUID.randomUUID()),
        cash = Money(500),
        income = Money(0),
        mpc = 0.8,
        employer = Some(employerId)
      )
    }

    World(firms, households)
  }
}
