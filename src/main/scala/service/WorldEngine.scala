package service

import cats.effect.{IO, Ref}
import cats.implicits._
import domain.Newtypes.{Money, Quantity, Sim}
import domain.Newtypes.Money._
import domain.SimulationOps.{investS, produceS}
import domain.{Firm, Household, World}

import scala.util.Random

object WorldEngine {
  private val strategy: Sim[Unit] = for {
    _ <- produceS(Money(100))
    _ <- investS(Money(1000))
  } yield ()

  private def runOneFirm(f: Firm): IO[Firm] = IO {
    strategy.run(f) match {
      case Left(err) => println(s"Firm ${f.id} failed: $err"); f
      case Right((newFirm, _)) => newFirm
    }
  }

  private def shop(h: Household, firms: List[Ref[IO, Firm]]): IO[Household] =
    h.decideSpending match {
      case 0 => IO(h)
      case budget => Random.shuffle(firms).head.modify { firm =>
        val q = math.min(firm.quantity.value, (budget / firm.price).value.toInt)
        val effectiveCost = Money(q * firm.price.value)

        (
          firm.copy(quantity = Quantity(firm.quantity.value - q).getOrElse(Quantity.unsafe(0)), cash = firm.cash + effectiveCost),
          h.copy(cash = h.cash - effectiveCost)
        )
      }
    }

  def nextStep(world: World): IO[World] =
    world.firms.parTraverse(runOneFirm).flatMap { runFirms =>
      runFirms.traverse(f => Ref.of[IO, Firm](f)).flatMap { firmRefs =>
        world.households.parTraverse(h => shop(h, firmRefs)).flatMap { updatedHouseholds =>
          firmRefs.traverse(_.get).map { finalFirms =>
            World(finalFirms, updatedHouseholds)
          }
        }
      }
    }
}
