package dev.alvo.todo.storage

import cats.effect.Sync
import cats.effect.concurrent.Ref
import cats.implicits._
import dev.alvo.todo.UUIDGenerator
import dev.alvo.todo.storage.model.Task

import scala.util.chaining._

object InMemoryTodoStorage {

  def dsl[F[_]: Sync: UUIDGenerator](storage: Ref[F, Map[String, Task.Existing]]): TodoStorage[F] =
    new TodoStorage[F] {
      override def add(task: Task.New): F[Option[Task.Existing]] =
        implicitly[UUIDGenerator[F]].generate
          .flatMap(_.toString.pipe(id => storage.update(_.updated(id, Task.Existing(id, task.action))) >> get(id)))

      override def get(id: String): F[Option[Task.Existing]] = storage.get.map(_.get(id))

      override def remove(id: String): F[Unit] = storage.update(_ - id)

      override def clear(): F[Unit] = storage.update(_ => Map.empty)

      override def getAll: F[List[Task.Existing]] = storage.get.map(s => s.values.toList)

      override def update(id: String, task: Task.New): F[Option[Task.Existing]] =
        storage.update(s => s.updated(id, Task.Existing(id, task.action))) >> get(id)
    }
}
