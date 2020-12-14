package dev.alvo.todo.controller

import cats.effect.{Concurrent, ContextShift, Sync}
import cats.syntax.functor._
import dev.alvo.todo.endpoints.OpenApiEndpoints
import dev.alvo.todo.routes.OpenAPIRoutes

object SwaggerController {
  def create[F[_]: Concurrent: ContextShift](
    openApiEndpoints: OpenApiEndpoints[F]
  )(implicit F: Sync[F]): F[Controller[F]] =
    OpenAPIRoutes.create(openApiEndpoints.endpoints).map(apiRoutes => Controller.fromRoutes(apiRoutes.routes))
}
