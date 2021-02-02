package dev.alvo.user

import cats.effect.ConcurrentEffect
import dev.alvo.user.controller.Controller
import org.http4s.HttpApp

object Entrypoint {
  import cats.syntax.semigroupk._
  import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT

  def forControllers[F[_]: ConcurrentEffect](first: Controller[F], remaining: Controller[F]*): HttpApp[F] =
    (first +: remaining)
      .map(_.routes)
      .reduceLeft(_ <+> _)
      .orNotFound
}
