package dev.alvo.todo.http

import cats.effect.{ConcurrentEffect, ContextShift}
import cats.effect.concurrent.Ref
import cats.syntax.all._
import dev.alvo.todo.config.Configuration
import dev.alvo.todo.database.MongoDb
import dev.alvo.todo.http.controller.TodoController
import dev.alvo.todo.storage.MongodbTodoStorage
import dev.alvo.todo.storage.model.Task
import org.http4s.HttpApp
import utils.UUIDGenerator

object HttpApplication {

  def createEntrypoint[F[_]: ConcurrentEffect: ContextShift](config: Configuration): F[HttpApp[F]] =
    for {
      generator <- UUIDGenerator[F]
//      storage <- Ref.of(Map.empty[String, Task.Existing])
//      service <- InMemoryTodoStorage[F](storage, generator)
      database <- MongoDb.dsl(config)
      service <- MongodbTodoStorage[F](database, generator)
      todoController <- TodoController.create(service)
    } yield Entrypoint.forControllers(todoController)
}
