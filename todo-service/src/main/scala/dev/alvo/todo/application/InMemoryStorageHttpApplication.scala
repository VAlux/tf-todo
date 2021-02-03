package dev.alvo.todo.application

import cats.effect.concurrent.Ref
import cats.effect.{ConcurrentEffect, ContextShift, Sync, Timer}
import cats.syntax.all._
import dev.alvo.shared.util.UUIDGenerator
import dev.alvo.todo.Entrypoint
import dev.alvo.todo.config.TodoConfiguration
import dev.alvo.todo.controller.{SwaggerController, TodoController}
import dev.alvo.todo.endpoints.OpenApiEndpoints
import dev.alvo.todo.endpoints.application.TodoEndpoints
import dev.alvo.todo.service.TodoService
import dev.alvo.todo.service.authentication.JwtAuthenticationService
import dev.alvo.todo.repository.InMemoryTodoRepository
import dev.alvo.todo.repository.model.Existing

object InMemoryStorageHttpApplication {

  def apply[F[_]: ConcurrentEffect: ContextShift: Timer](implicit F: Sync[F]): F[HttpApplication[F]] = F.delay {
    (_: TodoConfiguration) =>
      for {
        generator <- UUIDGenerator[F]
        engine <- Ref.of(Map.empty[String, Existing])
        storage <- InMemoryTodoRepository[F](engine, generator)
        todoService <- TodoService(storage)
        authenticationService <- JwtAuthenticationService.create[F]
        todoEndpoints = new TodoEndpoints[F](todoService, authenticationService)
        openApiEndpoints = new OpenApiEndpoints(todoEndpoints)
        todoController <- TodoController(todoEndpoints)
        swaggerController <- SwaggerController(openApiEndpoints)
      } yield Entrypoint.forControllers(todoController, swaggerController)
  }
}
