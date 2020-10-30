package dev.alvo.todo.http

import cats.effect.ConcurrentEffect
import cats.effect.concurrent.Ref
import dev.alvo.todo.http.controller.TodoController
import dev.alvo.todo.storage.InMemoryTodoStorage
import dev.alvo.todo.storage.model.Task
import org.http4s.HttpApp
import cats.syntax.all._

object App {

  def create[F[_]: ConcurrentEffect]: F[HttpApp[F]] =
    for {
      todoService <- Ref.of(Map.empty[String, Task.Existing]).flatMap(InMemoryTodoStorage[F])
      todoController <- TodoController.create(todoService)
    } yield Entrypoint.create(todoController)
}
