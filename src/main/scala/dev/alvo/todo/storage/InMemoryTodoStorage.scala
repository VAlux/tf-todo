package dev.alvo.todo.storage

import cats.effect.Sync
import cats.effect.concurrent.Ref
import cats.implicits._
import dev.alvo.todo.UUIDGenerator
import dev.alvo.todo.storage.model.Task
import dev.alvo.todo.storage.model.TasksModel

object InMemoryTodoStorage {

  def dsl[F[_]](storage: Ref[F, Map[String, Task]])(implicit F: Sync[F], Gen: UUIDGenerator[F]): TodoStorage[F] =
    new TodoStorage[F] {
      override def add(task: Task): F[Unit] = Gen.generate.flatMap(id => storage.update(_.updated(id.toString, task)))

      override def get(id: String): F[Option[Task]] = storage.get.map(_.get(id))

      override def remove(id: String): F[Unit] = storage.update(_ - id)

      override def clear(): F[Unit] = storage.update(_ => Map.empty)

      override def getAll: F[TasksModel] = storage.get.map(s => TasksModel(s.toList))
    }
}
