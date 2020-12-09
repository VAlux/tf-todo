package dev.alvo.todo.routes

import org.http4s.HttpRoutes

trait Routes[F[_]] {
  val routes: HttpRoutes[F]
}
