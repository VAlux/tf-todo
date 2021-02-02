package dev.alvo.todo.application

import cats.effect.{Async, ConcurrentEffect, ContextShift, Sync, Timer}
import cats.syntax.flatMap._
import cats.syntax.functor._
import dev.alvo.mongodb.MongoDb
import dev.alvo.shared.config.ConfigurationLoader
import dev.alvo.todo.Entrypoint
import dev.alvo.todo.config.TodoConfiguration
import dev.alvo.todo.controller.{SwaggerController, TodoController}
import dev.alvo.todo.endpoints.OpenApiEndpoints
import dev.alvo.todo.endpoints.application.TodoEndpoints
import dev.alvo.todo.service.TodoService
import dev.alvo.todo.service.authentication.JwtAuthenticationService
import dev.alvo.todo.storage.MongodbTodoStorage
import utils.UUIDGenerator

import scala.concurrent.ExecutionContext

object MongodbStorageHttpApplication {

  def apply[F[_]: ConcurrentEffect: ContextShift: Timer](
    ec: ExecutionContext
  )(implicit F: Sync[F]): F[HttpApplication[F]] = F.delay { (config: TodoConfiguration) =>
    for {
      generator <- UUIDGenerator[F]
      engine <- MongoDb(config.mongo)(implicitly[ContextShift[F]], implicitly[Async[F]], ec)
      storage <- MongodbTodoStorage[F](engine, generator)(implicitly[ContextShift[F]], implicitly[Async[F]], ec)
      authenticationService <- JwtAuthenticationService.create
      todoService <- TodoService.create(storage)
      todoEndpoints = new TodoEndpoints[F](todoService, authenticationService)
      openApiEndpoints = new OpenApiEndpoints(todoEndpoints)
      todoController <- TodoController.create(todoEndpoints)
      swaggerController <- SwaggerController.create(openApiEndpoints)
    } yield Entrypoint.forControllers(todoController, swaggerController)
  }
}
