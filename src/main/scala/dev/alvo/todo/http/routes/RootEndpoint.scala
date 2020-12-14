package dev.alvo.todo.http.routes

import dev.alvo.todo.http.model.response.RootErrorInfoResponse
import io.circe.generic.auto._
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._

class RootEndpoint {

  val rootV1: Endpoint[Unit, RootErrorInfoResponse, Unit, Any] =
    endpoint
      .in("api" / "v1.0")
      .errorOut(jsonBody[RootErrorInfoResponse])
}
