package dev.alvo.user.endpoints.application

import cats.effect.Sync
import cats.syntax.functor._
import dev.alvo.user.endpoints.RootEndpoint
import dev.alvo.user.model.User
import dev.alvo.user.model.request.RegisterUserRequest
import dev.alvo.user.model.response.ErrorResponse.UserErrorResponse.{NotFoundResponse, UserNotRegisteredResponse}
import dev.alvo.user.model.response.{ErrorResponse, RegisterUserResponse, RetrieveUserResponse}
import dev.alvo.user.service.user.UserService
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.{Endpoint, _}

class UserEndpoints[F[_]: Sync](userService: UserService[F]) extends ApplicationEndpoints[F] {

  private val userRoot: Endpoint[Unit, ErrorResponse, Unit, Any] =
    RootEndpoint.rootV1.in("user")

  val findUser: ServerEndpoint[String, ErrorResponse, RetrieveUserResponse, Any, F] =
    userRoot.get
      .description("Find user profile by email")
      .in(path[String]("email").description("User email"))
      .out(jsonBody[RetrieveUserResponse])
      .serverLogic(email => userService.findUserByEmail(email).map(toRetrieveUserResponse))

  val registerUser: ServerEndpoint[RegisterUserRequest, ErrorResponse, RegisterUserResponse, Any, F] =
    userRoot.post
      .description("Register new user")
      .in(jsonBody[RegisterUserRequest])
      .out(jsonBody[RegisterUserResponse])
      .serverLogic(request => userService.registerUser(User(request.email, request.name)).map(toRegisteredUserResponse))

  val disableUser: ServerEndpoint[String, ErrorResponse, RetrieveUserResponse, Any, F] =
    userRoot.patch
      .description("Disable user")
      .in(path[String]("email").description("User email"))
      .out(jsonBody[RetrieveUserResponse])
      .serverLogic(email => userService.disableUser(email).map(toRetrieveUserResponse))

  private def toResponse[I, R](input: Option[I], f: I => R, error: ErrorResponse): Either[ErrorResponse, R] =
    input.fold[Either[ErrorResponse, R]](Left(error))(input => Right(f(input)))

  private def toRetrieveUserResponse(user: Option[User]): Either[ErrorResponse, RetrieveUserResponse] =
    toResponse[User, RetrieveUserResponse](
      user,
      user => RetrieveUserResponse(user.email, user.name),
      NotFoundResponse()
    )

  private def toRegisteredUserResponse(user: Option[User]): Either[ErrorResponse, RegisterUserResponse] =
    toResponse[User, RegisterUserResponse](
      user,
      user => RegisterUserResponse(user.email, user.name),
      UserNotRegisteredResponse()
    )

  override def asSeq(): Seq[ServerEndpoint[_, _, _, _, F]] = Seq(findUser)
}
