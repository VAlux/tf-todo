package dev.alvo.todo.controller

import org.http4s.HttpRoutes

/**
  * Represents a group of routes, related to the same unit of functionality.
  * @tparam F effect type
  */
trait Controller[F[_]] {
  val routes: HttpRoutes[F]
}

object Controller {
  def fromRoutes[F[_]](sourceRoutes: HttpRoutes[F]): Controller[F] = new Controller[F] {
    override val routes: HttpRoutes[F] = sourceRoutes
  }
}
