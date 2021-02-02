package dev.alvo.todo.endpoints

import dev.alvo.todo.endpoints.application.ApplicationEndpoints
import sttp.tapir.docs.openapi.RichOpenAPIServerEndpoints
import sttp.tapir.openapi.OpenAPI

class OpenApiEndpoints[F[_]](first: ApplicationEndpoints[F], remaining: ApplicationEndpoints[F]*) {

  val endpoints: OpenAPI =
    (first +: remaining).flatMap(_.asSeq()).toOpenAPI("ToDo API", "1.0")
}
