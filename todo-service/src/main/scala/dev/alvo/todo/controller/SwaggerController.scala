package dev.alvo.todo.controller

import cats.effect.{Concurrent, ContextShift}
import cats.syntax.functor._
import dev.alvo.todo.endpoints.OpenApiEndpoints
import dev.alvo.todo.routes.OpenAPIRoutes

object SwaggerController {
  def apply[F[_]: Concurrent: ContextShift](openApiEndpoints: OpenApiEndpoints[F]): F[Controller[F]] =
    OpenAPIRoutes.create(openApiEndpoints.endpoints).map(apiRoutes => Controller.fromRoutes(apiRoutes.routes))
}
