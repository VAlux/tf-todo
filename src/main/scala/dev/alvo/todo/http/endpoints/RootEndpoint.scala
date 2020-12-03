package dev.alvo.todo.http.endpoints

import dev.alvo.todo.http.model.response.ErrorInfoResponse
import dev.alvo.todo.http.model.response.ErrorInfoResponse._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.{Endpoint, endpoint, _}

object RootEndpoint {

  val rootV1: Endpoint[Unit, ErrorInfoResponse, Unit, Any] =
    endpoint
      .in("api" / "v1.0")
      .errorOut(jsonBody[ErrorInfoResponse])
}
