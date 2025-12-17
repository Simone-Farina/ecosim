package service

import cats.effect.std.Random
import cats.effect.{IO, Ref}
import cats.implicits._
import domain.Newtypes.{BudgetOp, FirmId, Money, Quantity, Sim}
import domain.Newtypes.Money._
import domain.SimulationOps.{investS, produceS}
import domain.{Firm, Household, World}

object WorldEngine {
  private val strategy: Sim[Unit] = for {
    _ <- produceS(Money(100))
    _ <- investS(Money(1000))
  } yield ()

  private def runOneFirm(f: Firm): IO[Firm] = IO {
    strategy.run(f) match {
      case Left(err) =>
        println(s"Firm ${f.id} failed: $err")
        f
      case Right((newFirm, _)) => newFirm
    }
  }

  private def payWages(
      households: List[Household],
      firms: Map[FirmId, Ref[IO, Firm]]
  ): IO[List[Household]] =
    households.parTraverse { household =>
      household.employer
        .flatMap { firmId =>
          firms.get(firmId).map { firmRef =>
            firmRef
              .modify { firm =>
                {
                  val wages: Money = Money(firm.wage.value * firm.employees)
                  if (wages < firm.cash) {
                    (firm.copy(cash = firm.cash - wages), firm.wage)
                  } else (firm, firm.wage)
                }
              }
              .map { wage =>
                household.copy(
                  cash = household.cash + wage,
                  income = wage
                )
              }
          }
        }
        .getOrElse(household.copy(income = Money(0)).pure[IO])
    }

  private def shop(
      h: Household,
      firms: Map[FirmId, Ref[IO, Firm]]
  ): IO[Household] = {
    def visitFirm(firmRef: Ref[IO, Firm]): BudgetOp[Unit] = BudgetOp {
      currentBudget =>
        if (currentBudget.value <= 0) IO.pure((currentBudget, ()))
        else {
          firmRef
            .modify { firm =>
              val affordableUnits = math.min(
                firm.quantity.value,
                (currentBudget / firm.price).toInt
              )

              if (affordableUnits > 0) {
                val cost = Money(affordableUnits * firm.price.value)
                val newFirm = firm.copy(
                  quantity = Quantity(firm.quantity.value - affordableUnits)
                    .getOrElse(Quantity.unsafe(0)),
                  cash = firm.cash + cost
                )
                (newFirm, cost)
              } else {
                (firm, Money(0))
              }
            }
            .map { cost =>
              val newBudget = currentBudget - cost
              (newBudget, ())
            }
        }
    }

    for {
      shuffledFirms <- Random.apply[IO].shuffleList(firms.toList.map(_._2))
      initialBudget = h.planBudget
      shoppingProgram = shuffledFirms.traverse_(visitFirm)
      finalBudget <- shoppingProgram.runS(initialBudget)
      spent = initialBudget - finalBudget
    } yield h.copy(cash = h.cash - spent)
  }

  def nextStep(world: World): IO[World] =
    for {
      runFirms <- world.firms.parTraverse(runOneFirm)
      firmRefs <- runFirms.traverse(Ref.of[IO, Firm])
      firmsLookup <- firmRefs
        .traverse(ref => ref.get.map(firm => (firm.id, ref)))
        .map(_.toMap)
      paidHouseholds <- payWages(world.households, firmsLookup)
      world <- IO(world.copy(households = paidHouseholds))
      updatedHouseholds <- world.households.parTraverse(h =>
        shop(h, firmsLookup)
      )
      finalFirms <- firmsLookup.values.toList.traverse(_.get)
    } yield World(finalFirms, updatedHouseholds)
}
