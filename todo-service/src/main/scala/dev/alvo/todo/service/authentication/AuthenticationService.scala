package dev.alvo.todo.service.authentication

import dev.alvo.todo.model.User
import dev.alvo.todo.model.authentication.AuthenticationServiceDescriptor
import dev.alvo.todo.model.response.ErrorResponse

trait AuthenticationService[F[_], Descriptor <: AuthenticationServiceDescriptor] {
  def authenticate(descriptor: Descriptor): F[Either[ErrorResponse, User]]
}
