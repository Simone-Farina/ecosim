package domain

import cats.data.StateT
import domain.Newtypes.{Money, Sim}

object SimulationOps {
  def produceS(budget: Money): Sim[Unit] = StateT { currentFirm =>
    currentFirm.produce(budget).map((_, ()))
  }

  def investS(budget: Money): Sim[Unit] = StateT { currentFirm =>
    currentFirm.investInRnD(budget).map((_, ()))
  }

}
