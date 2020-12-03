package dev.alvo.todo.http.routes

import dev.alvo.todo.http.model.response.ErrorInfoResponse
import io.circe.generic.auto._
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._

object RootEndpoint {

  val rootV1: Endpoint[Unit, ErrorInfoResponse, Unit, Any] =
    endpoint
      .in("api" / "v1.0")
      .errorOut(jsonBody[ErrorInfoResponse])
}
