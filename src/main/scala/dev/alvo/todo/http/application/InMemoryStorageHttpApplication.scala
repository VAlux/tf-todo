package dev.alvo.todo.http.application

import cats.effect.concurrent.Ref
import cats.effect.{ConcurrentEffect, ContextShift, Sync, Timer}
import cats.syntax.all._
import dev.alvo.todo.config.Configuration
import dev.alvo.todo.http.Entrypoint
import dev.alvo.todo.http.controller.TodoController
import dev.alvo.todo.service.TodoService
import dev.alvo.todo.storage.InMemoryTodoStorage
import dev.alvo.todo.storage.model.Task
import utils.UUIDGenerator

object InMemoryStorageHttpApplication {

  def apply[F[_]: ConcurrentEffect: ContextShift: Timer](implicit F: Sync[F]): F[HttpApplication[F]] = F.delay {
    (_: Configuration) =>
      for {
        generator <- UUIDGenerator[F]
        engine <- Ref.of(Map.empty[String, Task.Existing])
        storage <- InMemoryTodoStorage[F](engine, generator)
        service <- TodoService.create(storage)
        todoController <- TodoController.create(service)
      } yield Entrypoint.forControllers(todoController)
  }
}
