package dev.alvo.todo.service

import cats.effect.Sync
import dev.alvo.todo.model.User
import dev.alvo.todo.model.response.{ErrorResponse, UnauthorizedResponse}

trait AuthenticationService[F[_]] {
  def authenticate(token: String): F[Either[ErrorResponse, User]]
}

object AuthenticationService {
  def create[F[_]](implicit F: Sync[F]): F[AuthenticationService[F]] =
    F.delay((token: String) => dummySecurity(token))

  private def dummySecurity[F[_]](token: String)(implicit F: Sync[F]): F[Either[ErrorResponse, User]] =
    F.delay {
      if (token == "secret") Right(User("email@email.com", "admin"))
      else Left(UnauthorizedResponse())
    }
}