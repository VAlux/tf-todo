package dev.alvo.todo.http.routes

import org.http4s.HttpRoutes

trait Routes[F[_]] {
  val routes: HttpRoutes[F]
}
