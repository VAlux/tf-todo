package dev.alvo.todo.application

import cats.effect.concurrent.Ref
import cats.effect.{ConcurrentEffect, ContextShift, Sync, Timer}
import cats.syntax.all._
import dev.alvo.todo.Entrypoint
import dev.alvo.todo.config.Configuration
import dev.alvo.todo.controller.{SwaggerController, TodoController}
import dev.alvo.todo.endpoints.OpenApiEndpoints
import dev.alvo.todo.endpoints.application.TodoEndpoints
import dev.alvo.todo.service.TodoService
import dev.alvo.todo.service.authentication.JwtAuthenticationService
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
        todoService <- TodoService.create(storage)
        authenticationService <- JwtAuthenticationService.create[F]
        todoEndpoints = new TodoEndpoints[F](todoService, authenticationService)
        openApiEndpoints = new OpenApiEndpoints(todoEndpoints)
        todoController <- TodoController.create(todoEndpoints)
        swaggerController <- SwaggerController.create(openApiEndpoints)
      } yield Entrypoint.forControllers(todoController, swaggerController)
  }
}
