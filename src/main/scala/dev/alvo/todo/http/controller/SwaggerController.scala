package dev.alvo.todo.http.controller

import cats.effect.{Concurrent, ContextShift, Sync}
import cats.syntax.functor._
import dev.alvo.todo.http.routes.OpenAPIRoutes

object SwaggerController {
  def create[F[_]: Concurrent: ContextShift](implicit F: Sync[F]): F[Controller[F]] =
    OpenAPIRoutes.create.map(apiRoutes => Controller.fromRoutes(apiRoutes.routes))
}
