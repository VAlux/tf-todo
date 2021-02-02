package dev.alvo.todo.storage

import cats.effect.Sync
import cats.effect.concurrent.Ref
import cats.implicits._
import dev.alvo.shared.util.UUIDGenerator
import dev.alvo.todo.storage.model.{Existing, New, Task}

object InMemoryTodoStorage {
  def apply[F[_]](storage: Ref[F, Map[String, Existing]], uuid: UUIDGenerator[F])(
    implicit
    F: Sync[F]
  ): F[TodoStorage[F]] =
    F.delay {
      new TodoStorage[F] {
        override def add(task: New): F[Option[Existing]] =
          for {
            id <- uuid.generate.map(_.toString)
            _ <- storage.update(_.updated(id, Existing(id, task.action)))
            added <- get(id)
          } yield added

        override def get(id: String): F[Option[Existing]] = storage.get.map(_.get(id))

        override def remove(id: String): F[Unit] = storage.update(_ - id)

        override def clear(): F[Unit] = storage.update(_ => Map.empty)

        override def getAll: F[List[Existing]] = storage.get.map(s => s.values.toList)

        override def update(id: String, task: New): F[Option[Existing]] =
          storage.update(_.updated(id, Existing(id, task.action))) >> get(id)
      }
    }
}
