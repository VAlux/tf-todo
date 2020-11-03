package dev.alvo.todo.http.application

import cats.effect.concurrent.Ref
import cats.effect.{ConcurrentEffect, Sync}
import dev.alvo.todo.config.Configuration
import dev.alvo.todo.http.Entrypoint
import dev.alvo.todo.http.controller.TodoController
import dev.alvo.todo.storage.InMemoryTodoStorage
import dev.alvo.todo.storage.model.Task
import utils.UUIDGenerator
import cats.syntax.all._

object InMemoryStorageHttpApplication {

  def apply[F[_]: ConcurrentEffect](implicit F: Sync[F]): F[HttpApplication[F]] = F.delay { (_: Configuration) =>
    for {
      generator <- UUIDGenerator[F]
      storage <- Ref.of(Map.empty[String, Task.Existing])
      service <- InMemoryTodoStorage[F](storage, generator)
      todoController <- TodoController.create(service)
    } yield Entrypoint.forControllers(todoController)
  }
}
