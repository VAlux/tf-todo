package dev.alvo.todo.service.authentication

import cats.effect.Sync
import dev.alvo.todo.model.User
import dev.alvo.todo.model.authentication.BasicAuthenticationServiceDescriptor
import dev.alvo.todo.model.response.{ErrorResponse, UnauthorizedResponse}
import com.github.t3hnar.bcrypt._

trait BasicAuthenticationService[F[_]] extends AuthenticationService[F, BasicAuthenticationServiceDescriptor]

object BasicAuthenticationService {
  def create[F[_]](implicit F: Sync[F]): F[BasicAuthenticationService[F]] = F.delay {
    (descriptor: BasicAuthenticationServiceDescriptor) =>
      authenticate(descriptor.username, descriptor.password)
  }

  def authenticate[F[_]](username: String, password: Option[String])(
    implicit
    F: Sync[F]
  ): F[Either[ErrorResponse, User]] =
    F.delay(dummyAuthentication(username, password))

  private def dummyAuthentication[F[_]](
    username: String,
    password: Option[String]
  ): Either[UnauthorizedResponse, User] =
    if (username == "admin" && password.exists(hash => "pass".isBcryptedSafe(hash).getOrElse(false)))
      Right(User("email@email.com", "admin"))
    else
      Left(UnauthorizedResponse())
}
