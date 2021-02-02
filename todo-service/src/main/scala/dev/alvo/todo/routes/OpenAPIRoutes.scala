package dev.alvo.todo.routes

import cats.effect.{Concurrent, ContextShift, Sync}
import org.http4s.HttpRoutes
import sttp.tapir.openapi.OpenAPI
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.swagger.http4s.SwaggerHttp4s

object OpenAPIRoutes {
  def create[F[_]: Concurrent: ContextShift](openApi: OpenAPI)(implicit F: Sync[F]): F[Routes[F]] =
    F.delay {
      new Routes[F] {
        override val routes: HttpRoutes[F] = new SwaggerHttp4s(openApi.toYaml).routes
      }
    }
}
