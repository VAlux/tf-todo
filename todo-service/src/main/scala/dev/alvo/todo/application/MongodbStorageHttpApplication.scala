package dev.alvo.todo.application

import cats.effect.{Async, ConcurrentEffect, ContextShift, Sync, Timer}
import cats.syntax.flatMap._
import cats.syntax.functor._
import dev.alvo.mongodb.MongoDb
import dev.alvo.shared.util.UUIDGenerator
import dev.alvo.todo.Entrypoint
import dev.alvo.todo.config.TodoConfiguration
import dev.alvo.todo.controller.{SwaggerController, TodoController}
import dev.alvo.todo.endpoints.OpenApiEndpoints
import dev.alvo.todo.endpoints.application.TodoEndpoints
import dev.alvo.todo.repository.MongodbTodoRepository
import dev.alvo.todo.service.TodoService
import dev.alvo.todo.service.authentication.JwtAuthenticationService

import scala.concurrent.ExecutionContext

object MongodbStorageHttpApplication {

  def apply[F[_]: ConcurrentEffect: ContextShift: Timer](
    ec: ExecutionContext
  )(implicit F: Sync[F]): F[HttpApplication[F]] = F.delay { (config: TodoConfiguration) =>
    for {
      generator <- UUIDGenerator[F]
      engine <- MongoDb(config.mongo)(implicitly[ContextShift[F]], implicitly[Async[F]], ec)
      storage <- MongodbTodoRepository[F](engine, generator)(implicitly[ContextShift[F]], implicitly[Async[F]], ec)
      authenticationService <- JwtAuthenticationService.create
      todoService <- TodoService(storage)
      todoEndpoints = new TodoEndpoints[F](todoService, authenticationService)
      openApiEndpoints = new OpenApiEndpoints(todoEndpoints)
      todoController <- TodoController(todoEndpoints)
      swaggerController <- SwaggerController(openApiEndpoints)
    } yield Entrypoint.forControllers(todoController, swaggerController)
  }
}
