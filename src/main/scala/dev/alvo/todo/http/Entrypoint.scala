package dev.alvo.todo.http

import cats.MonoidK.ops.toAllMonoidKOps
import cats.effect.ConcurrentEffect
import dev.alvo.todo.http.controller.Controller
import org.http4s.HttpApp
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.server.Router

import scala.util.chaining._

object Entrypoint {

  def create[F[_]: ConcurrentEffect](first: Controller[F], remaining: Controller[F]*): HttpApp[F] =
    (first +: remaining)
      .map(_.routes)
      .reduceLeft(_ <+> _)
      .pipe(routes => Router("api" -> routes))
      .orNotFound
}
