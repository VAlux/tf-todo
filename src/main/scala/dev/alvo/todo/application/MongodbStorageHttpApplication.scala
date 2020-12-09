package dev.alvo.todo.application

import cats.effect.{ConcurrentEffect, ContextShift, Sync, Timer}
import cats.syntax.flatMap._
import cats.syntax.functor._
import dev.alvo.todo.Entrypoint
import dev.alvo.todo.config.Configuration
import dev.alvo.todo.database.MongoDb
import dev.alvo.todo.controller.{SwaggerController, TodoController}
import dev.alvo.todo.endpoints.{OpenApiEndpoints, TodoEndpoints}
import dev.alvo.todo.service.{AuthenticationService, TodoService}
import dev.alvo.todo.storage.MongodbTodoStorage
import utils.UUIDGenerator

object MongodbStorageHttpApplication {

  def apply[F[_]: ConcurrentEffect: ContextShift: Timer](implicit F: Sync[F]): F[HttpApplication[F]] = F.delay {
    (config: Configuration) =>
      for {
        generator <- UUIDGenerator[F]
        engine <- MongoDb.dsl(config)
        storage <- MongodbTodoStorage[F](engine, generator)
        authenticationService <- AuthenticationService.create
        todoService <- TodoService.create(storage)
        todoEndpoints = new TodoEndpoints[F](todoService, authenticationService)
        openApiEndpoints = new OpenApiEndpoints(todoEndpoints)
        todoController <- TodoController.create(todoEndpoints)
        swaggerController <- SwaggerController.create(openApiEndpoints)
      } yield Entrypoint.forControllers(todoController, swaggerController)
  }
}
