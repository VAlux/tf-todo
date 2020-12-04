package dev.alvo.todo.http

import cats.effect.ConcurrentEffect
import cats.syntax.semigroupk._
import dev.alvo.todo.http.controller.Controller
import org.http4s.HttpApp
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT

object Entrypoint {

  def forControllers[F[_]: ConcurrentEffect](first: Controller[F], remaining: Controller[F]*): HttpApp[F] =
    (first +: remaining)
      .map(_.routes)
      .reduceLeft(_ <+> _)
      .orNotFound
}
