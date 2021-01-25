package dev.alvo.todo.endpoints

import dev.alvo.todo.model.User
import dev.alvo.todo.model.authentication.JwtAuthenticationServiceDescriptor
import dev.alvo.todo.model.response._
import dev.alvo.todo.service.authentication.JwtAuthenticationService
import io.circe.generic.auto._
import sttp.model.StatusCode
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.PartialServerEndpoint
import sttp.tapir.{endpoint, _}

object RootEndpoint {

  val rootV1: Endpoint[Unit, ErrorResponse, Unit, Any] =
    endpoint
      .description("Main entry point of the To Do API")
      .name("To Do API")
      .in("api" / "v1")
      .errorOut(errorResponseMapping)

  def secureRootV1[F[_]](
    authenticationService: JwtAuthenticationService[F]
  ): PartialServerEndpoint[User, Unit, ErrorResponse, Unit, Any, F] =
    rootV1
      .in(auth.bearer[String])
      .serverLogicForCurrent(token => authenticationService.authenticate(JwtAuthenticationServiceDescriptor(token)))

  private lazy val errorResponseMapping: EndpointOutput.OneOf[ErrorResponse, ErrorResponse] =
    oneOf[ErrorResponse](
      statusMapping(
        StatusCode.NotFound,
        jsonBody[NotFoundResponse].description("Not found")
      ),
      statusMapping(
        StatusCode.BadRequest,
        jsonBody[BadRequestResponse].description("Bad request")
      ),
      statusMapping(
        StatusCode.Unauthorized,
        jsonBody[UnauthorizedResponse].description("Unauthorized")
      ),
      statusMapping(
        StatusCode.InternalServerError,
        jsonBody[InternalErrorResponse].description("Internal server error")
      )
    )
}
