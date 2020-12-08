package dev.alvo.todo.http.application

import cats.effect.{ConcurrentEffect, ContextShift, Sync, Timer}
import cats.syntax.flatMap._
import cats.syntax.functor._
import dev.alvo.todo.config.Configuration
import dev.alvo.todo.database.MongoDb
import dev.alvo.todo.http.Entrypoint
import dev.alvo.todo.http.controller.{SwaggerController, TodoController}
import dev.alvo.todo.http.endpoints.{OpenApiEndpoints, TodoEndpoints}
import dev.alvo.todo.service.TodoService
import dev.alvo.todo.storage.MongodbTodoStorage
import utils.UUIDGenerator

object MongodbStorageHttpApplication {

  def apply[F[_]: ConcurrentEffect: ContextShift: Timer](implicit F: Sync[F]): F[HttpApplication[F]] = F.delay {
    (config: Configuration) =>
      for {
        generator <- UUIDGenerator[F]
        engine <- MongoDb.dsl(config)
        storage <- MongodbTodoStorage[F](engine, generator)
        service <- TodoService.create(storage)
        todoEndpoints = new TodoEndpoints(service)
        openApiEndpoints = new OpenApiEndpoints(todoEndpoints)
        todoController <- TodoController.create(todoEndpoints)
        swaggerController <- SwaggerController.create(openApiEndpoints)
      } yield Entrypoint.forControllers(todoController, swaggerController)
  }
}
