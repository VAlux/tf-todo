package dev.alvo.todo.service.authentication

import cats.effect.Sync
import dev.alvo.todo.model.User
import dev.alvo.todo.model.authentication.BasicAuthenticationServiceDescriptor
import dev.alvo.todo.model.response.{ErrorResponse, UnauthorizedResponse}

trait BasicAuthenticationService[F[_]] extends AuthenticationService[F, BasicAuthenticationServiceDescriptor]

object BasicAuthenticationService {
  def create[F[_]](implicit F: Sync[F]): F[BasicAuthenticationService[F]] = F.delay(
    (descriptor: BasicAuthenticationServiceDescriptor) => dummySecurity(descriptor.username, descriptor.password)
  )

  def dummySecurity[F[_]](username: String, password: String)(implicit F: Sync[F]): F[Either[ErrorResponse, User]] =
    F.delay {
      if (username == "admin" && password == "secret") Right(User("email@email.com", "admin"))
      else Left(UnauthorizedResponse())
    }
}
