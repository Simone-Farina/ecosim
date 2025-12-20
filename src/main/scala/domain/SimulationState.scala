package domain

import cats.effect.{Ref, IO}
import domain.models.World

final case class SimulationState(
    worldRef: Ref[IO, World],
    pausedRef: Ref[IO, Boolean]
)

object SimulationState {
  def make(initialWorld: World): IO[SimulationState] =
    for {
      w <- Ref.of[IO, World](initialWorld)
      p <- Ref.of[IO, Boolean](false)
    } yield SimulationState(w, p)
}
