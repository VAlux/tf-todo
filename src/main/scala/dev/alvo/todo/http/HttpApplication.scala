package dev.alvo.todo.http

import cats.effect.ConcurrentEffect
import cats.effect.concurrent.Ref
import cats.syntax.all._
import dev.alvo.todo.http.controller.TodoController
import dev.alvo.todo.storage.InMemoryTodoStorage
import dev.alvo.todo.storage.model.Task
import org.http4s.HttpApp
import utils.UUIDGenerator

object HttpApplication {

  def createEntrypoint[F[_]: ConcurrentEffect]: F[HttpApp[F]] =
    for {
      generator <- UUIDGenerator[F]
      storage <- Ref.of(Map.empty[String, Task.Existing])
      service <- InMemoryTodoStorage[F](storage, generator)
      todoController <- TodoController.create(service)
    } yield Entrypoint.forControllers(todoController)
}
