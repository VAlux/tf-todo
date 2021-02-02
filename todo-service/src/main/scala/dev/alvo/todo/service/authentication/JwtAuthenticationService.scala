package dev.alvo.todo.service.authentication

import cats.effect.Sync
import dev.alvo.todo.model.User
import dev.alvo.todo.model.authentication.JwtAuthenticationServiceDescriptor
import dev.alvo.todo.model.response.{ErrorResponse, UnauthorizedResponse}

trait JwtAuthenticationService[F[_]] extends AuthenticationService[F, JwtAuthenticationServiceDescriptor]

object JwtAuthenticationService {
  def create[F[_]](implicit F: Sync[F]): F[JwtAuthenticationService[F]] =
    F.delay((descriptor: JwtAuthenticationServiceDescriptor) => dummySecurity(descriptor.token))

  private def dummySecurity[F[_]](token: String)(implicit F: Sync[F]): F[Either[ErrorResponse, User]] =
    F.delay {
      if (token == "secret") Right(User("email@email.com", "admin"))
      else Left(UnauthorizedResponse())
    }
}
