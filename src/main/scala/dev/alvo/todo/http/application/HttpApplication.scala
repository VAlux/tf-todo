package dev.alvo.todo.http.application

import dev.alvo.todo.config.Configuration
import org.http4s.HttpApp

trait HttpApplication[F[_]] {
  def createEntrypoint(config: Configuration): F[HttpApp[F]]
}
