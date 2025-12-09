package service

import cats.effect.std.Random
import cats.effect.{IO, Ref}
import cats.implicits._
import domain.Newtypes.{Money, Quantity, Sim}
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
      case Left(err)           =>
        println(s"Firm ${f.id} failed: $err")
        f
      case Right((newFirm, _)) => newFirm
    }
  }

  private def shop(h: Household, firms: List[Ref[IO, Firm]]): IO[Household] = {
    def buy(remainingFirms: List[Ref[IO, Firm]], budget: Money): IO[Money] = remainingFirms.traverse { firmRef =>
      firmRef.modify { firm =>
        val affordableUnits = math.min(firm.quantity.value, (budget / firm.price).toInt)

        if (affordableUnits > 0) {
          val effectiveCost = Money(affordableUnits * firm.price.value)
          (
            firm.copy(
              quantity = Quantity(firm.quantity.value - affordableUnits).getOrElse(Quantity.unsafe(0)),
              cash = firm.cash + effectiveCost,
            ),
            budget - effectiveCost,
          )
        } else (firm, budget)
      }
    }.map(_.fold(Money(0)) {
      case (acc, curr) => acc + curr
    })

    h.planBudget match {
      case 0      => IO(h)
      case budget =>
        for {
          shuffledFirms <- Random.apply[IO].shuffleList(firms)
          bought        <- buy(shuffledFirms, budget)
        } yield h.copy(cash = h.cash - bought)
    }
  }

  def nextStep(world: World): IO[World] =
    for {
      runFirms          <- world.firms.parTraverse(runOneFirm)
      firmRefs          <- runFirms.traverse(f => Ref.of[IO, Firm](f))
      updatedHouseholds <- world.households.parTraverse(h => shop(h, firmRefs))
      finalFirms        <- firmRefs.traverse(_.get)
    } yield World(finalFirms, updatedHouseholds)
}
