package dev.alvo.todo.storage

import cats.effect.Sync
import cats.effect.concurrent.Ref
import cats.implicits._
import dev.alvo.todo.storage.model.Task
import utils.UUIDGenerator

object InMemoryTodoStorage {
  def apply[F[_]](storage: Ref[F, Map[String, Task.Existing]], uuid: UUIDGenerator[F])(
    implicit F: Sync[F]
  ): F[TodoStorage[F]] =
    F.delay {
      new TodoStorage[F] {
        override def add(task: Task.New): F[Option[Task.Existing]] =
          for {
            id <- uuid.generate.map(_.toString)
            _ <- storage.update(_.updated(id, Task.Existing(id, task.action)))
            added <- get(id)
          } yield added

        override def get(id: String): F[Option[Task.Existing]] = storage.get.map(_.get(id))

        override def remove(id: String): F[Unit] = storage.update(_ - id)

        override def clear(): F[Unit] = storage.update(_ => Map.empty)

        override def getAll: F[List[Task.Existing]] = storage.get.map(s => s.values.toList)

        override def update(id: String, task: Task.New): F[Option[Task.Existing]] =
          storage.update(_.updated(id, Task.Existing(id, task.action))) >> get(id)
      }
    }
}
