package dev.alvo.todo

import cats.effect.ConcurrentEffect
import dev.alvo.todo.controller.Controller
import org.http4s.HttpApp
import cats.syntax.semigroupk._
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT

object Entrypoint {

  def forControllers[F[_]: ConcurrentEffect](first: Controller[F], remaining: Controller[F]*): HttpApp[F] =
    (first +: remaining)
      .map(_.routes)
      .reduceLeft(_ <+> _)
      .orNotFound
}
