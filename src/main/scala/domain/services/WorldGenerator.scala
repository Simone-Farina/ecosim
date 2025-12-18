package domain.services

import domain.Newtypes.{FirmId, HouseholdId, Money, Quantity}
import domain.models.{Firm, Household, World}

import java.util.UUID

object WorldGenerator {
  def generate(numFirms: Int, numHouseholds: Int): World = {
    val firms = (1 to numFirms).toList.map { i =>
      Firm(
        id = FirmId(UUID.randomUUID()),
        cash = Money(1000),
        quantity = Quantity.unsafe(0),
        price = Money(10),
        debt = Money(0),
        tech = 1.0,
        employees = 2,
        wage = Money(1200)
      )
    }

    val households = (1 to numFirms).toList.map { i =>
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
