package service

import cats.effect.IO
import cats.implicits._
import domain.{Firm, World}
import domain.Newtypes.{Money, Sim}
import domain.SimulationOps.{investS, produceS}

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

  def nextStep(world: World): IO[World] = {
    (world.firms.parTraverse(runOneFirm), world.households.parTraverse(IO(_))).tupled.map {
      case (newFirms, sameHouseholds) => World(
        firms = newFirms,
        households = sameHouseholds
      )
    }
  }
}
