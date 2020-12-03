package dev.alvo.todo.http.routes

import cats.effect.{Concurrent, ContextShift, Sync}
import dev.alvo.todo.http.endpoints.OpenApiEndpoints
import org.http4s.HttpRoutes
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.swagger.http4s.SwaggerHttp4s

object OpenAPIRoutes {
  def create[F[_]: Concurrent: ContextShift](implicit F: Sync[F]): F[Routes[F]] = F.delay {
    new Routes[F] {
      override val routes: HttpRoutes[F] =
        new SwaggerHttp4s(OpenApiEndpoints.openApiEndpoints.toYaml).routes
    }
  }
}
