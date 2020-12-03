package dev.alvo.todo.http.application

import cats.effect.{ConcurrentEffect, ContextShift, Sync, Timer}
import dev.alvo.todo.config.Configuration
import dev.alvo.todo.database.MongoDb
import dev.alvo.todo.http.Entrypoint
import dev.alvo.todo.http.controller.TodoController
import dev.alvo.todo.storage.MongodbTodoStorage
import utils.UUIDGenerator
import cats.syntax.all._
import dev.alvo.todo.service.TodoService

object MongodbStorageHttpApplication {

  def apply[F[_]: ConcurrentEffect: ContextShift: Timer](implicit F: Sync[F]): F[HttpApplication[F]] = F.delay {
    (config: Configuration) =>
      for {
        generator <- UUIDGenerator[F]
        engine <- MongoDb.dsl(config)
        storage <- MongodbTodoStorage[F](engine, generator)
        service <- TodoService.create(storage)
        todoController <- TodoController.create(service)
      } yield Entrypoint.forControllers(todoController)
  }
}
