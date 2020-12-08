package dev.alvo.todo.http.endpoints

import dev.alvo.todo.http.model.User
import dev.alvo.todo.http.model.response.ErrorResponse
import dev.alvo.todo.http.model.response.ErrorResponse._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.PartialServerEndpoint
import sttp.tapir.{endpoint, _}

object RootEndpoint {

  def authenticate[F[_]](token: String): F[Either[ErrorResponse, User]] = ???

  def rootV1[F[_]]: Endpoint[Unit, ErrorResponse, Unit, Any] =
    endpoint
      .description("Main entry point of the To Do API")
      .name("To Do API")
      .in("api" / "v1.0")
      .errorOut(jsonBody[ErrorResponse])

  def secureRootV1[F[_]]: PartialServerEndpoint[User, Unit, ErrorResponse, Unit, Any, F] =
    rootV1
      .in(auth.bearer[String])
      .serverLogicForCurrent(authenticate[F])
}
