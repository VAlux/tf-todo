package dev.alvo.todo.http.controller

import cats.effect.{Concurrent, ContextShift, Sync}
import cats.syntax.flatMap._
import cats.syntax.functor._
import dev.alvo.todo.http.endpoints.{OpenApiEndpoints, TodoEndpoints}
import dev.alvo.todo.http.routes.OpenAPIRoutes
import org.http4s.HttpRoutes
import sttp.tapir.openapi.OpenAPI

object SwaggerController {
  def create[F[_]: Concurrent: ContextShift](
    openApiEndpoints: OpenApiEndpoints[F]
  )(implicit F: Sync[F]): F[Controller[F]] =
    OpenAPIRoutes.create(openApiEndpoints.endpoints).map(apiRoutes => Controller.fromRoutes(apiRoutes.routes))
}
