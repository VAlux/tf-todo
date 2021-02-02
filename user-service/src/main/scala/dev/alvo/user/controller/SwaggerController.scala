package dev.alvo.user.controller

import cats.effect.{Concurrent, ContextShift, Sync}
import cats.syntax.functor._
import dev.alvo.user.endpoints.OpenApiEndpoints
import dev.alvo.user.routes.OpenApiRoutes

object SwaggerController {
  def apply[F[_]: Concurrent: ContextShift](
    openApiEndpoints: OpenApiEndpoints[F]
  )(implicit F: Sync[F]): F[Controller[F]] =
    OpenApiRoutes.create(openApiEndpoints.endpoints).map(apiRoutes => Controller.fromRoutes(apiRoutes.routes))
}
