package dev.alvo.todo.http.controller

import org.http4s.HttpRoutes

trait Controller[F[_]] {
  val routes: HttpRoutes[F]
}
