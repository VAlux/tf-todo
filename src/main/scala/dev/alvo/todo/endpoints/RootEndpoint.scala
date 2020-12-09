package dev.alvo.todo.endpoints

import dev.alvo.todo.model.User
import dev.alvo.todo.model.response.ErrorResponse
import dev.alvo.todo.model.response.ErrorResponse._
import dev.alvo.todo.service.AuthenticationService
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.PartialServerEndpoint
import sttp.tapir.{endpoint, _}

object RootEndpoint {

  def rootV1[F[_]]: Endpoint[Unit, ErrorResponse, Unit, Any] =
    endpoint
      .description("Main entry point of the To Do API")
      .name("To Do API")
      .in("api" / "v1.0")
      .errorOut(jsonBody[ErrorResponse])

  def secureRootV1[F[_]](
    authenticationService: AuthenticationService[F]
  ): PartialServerEndpoint[User, Unit, ErrorResponse, Unit, Any, F] =
    rootV1
      .in(auth.bearer[String])
      .serverLogicForCurrent(authenticationService.authenticate)
}
