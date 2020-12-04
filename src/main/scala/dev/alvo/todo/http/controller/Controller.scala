package dev.alvo.todo.http.controller

import org.http4s.HttpRoutes

trait Controller[F[_]] {
  val routes: HttpRoutes[F]
}

object Controller {
  def fromRoutes[F[_]](sourceRoutes: HttpRoutes[F]): Controller[F] = new Controller[F] {
    override val routes: HttpRoutes[F] = sourceRoutes
  }
}
