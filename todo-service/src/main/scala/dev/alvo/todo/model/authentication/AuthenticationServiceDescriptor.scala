package dev.alvo.todo.model.authentication

sealed trait AuthenticationServiceDescriptor

case class BasicAuthenticationServiceDescriptor(username: String, password: Option[String])
    extends AuthenticationServiceDescriptor

case class JwtAuthenticationServiceDescriptor(token: String) extends AuthenticationServiceDescriptor
