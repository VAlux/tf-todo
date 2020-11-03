package dev.alvo.todo.http.application

import cats.effect.{ConcurrentEffect, ContextShift, Sync}
import dev.alvo.todo.config.Configuration
import dev.alvo.todo.database.MongoDb
import dev.alvo.todo.http.Entrypoint
import dev.alvo.todo.http.controller.TodoController
import dev.alvo.todo.storage.MongodbTodoStorage
import utils.UUIDGenerator
import cats.syntax.all._

object MongodbStorageHttpApplication {

  def apply[F[_]: ConcurrentEffect: ContextShift](implicit F: Sync[F]): F[HttpApplication[F]] = F.delay {
    (config: Configuration) =>
      for {
        generator <- UUIDGenerator[F]
        database <- MongoDb.dsl(config)
        service <- MongodbTodoStorage[F](database, generator)
        todoController <- TodoController.create(service)
      } yield Entrypoint.forControllers(todoController)
  }
}
