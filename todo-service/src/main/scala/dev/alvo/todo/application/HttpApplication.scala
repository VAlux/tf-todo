package dev.alvo.todo.application

import dev.alvo.todo.config.TodoConfiguration
import org.http4s.HttpApp

trait HttpApplication[F[_]] {
  def createEntrypoint(config: TodoConfiguration): F[HttpApp[F]]
}
