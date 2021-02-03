package dev.alvo.todo.repository

import cats.effect.{Async, ContextShift}
import dev.alvo.mongodb.MongoDb
import dev.alvo.mongodb.MongoDb.FutureOps
import dev.alvo.shared.util.UUIDGenerator
import dev.alvo.todo.repository.model.{Existing, New}
import jdk.internal.org.jline.utils.ShutdownHooks.Task
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, Macros}

import scala.concurrent.ExecutionContext

object MongodbTodoRepository {

  import cats.syntax.flatMap._
  import cats.syntax.functor._

  def apply[F[_]: ContextShift](mongoDsl: MongoDb[F], uuid: UUIDGenerator[F])(
    implicit
    F: Async[F],
    ec: ExecutionContext
  ): F[TodoRepository[F]] =
    mongoDsl.getDatabase("todo").map(_.collection[BSONCollection]("todos")).map { todos =>
      new TodoRepository[F] {

        implicit val taskWriter: BSONDocumentWriter[Existing] = Macros.writer[Existing]
        implicit val taskReader: BSONDocumentReader[Existing] = Macros.reader[Existing]

        private[this] def idFilter(id: String): BSONDocument = BSONDocument("id" -> id)

        override def add(task: New): F[Option[Existing]] =
          for {
            id <- uuid.generate
            _ <- todos.insert.one(Existing(id.toString, task.action)).asAsyncEffect
            added <- get(id.toString)
          } yield added

        override def get(id: String): F[Option[Existing]] =
          todos.find(idFilter(id)).one[Existing].asAsyncEffect

        override def update(id: String, task: New): F[Option[Existing]] =
          todos
            .findAndUpdate(
              idFilter(id),
              BSONDocument(f"$$set" -> BSONDocument("action" -> task.action)),
              fetchNewObject = true
            )
            .map(_.result)
            .asAsyncEffect

        override def getAll: F[List[Existing]] =
          todos.find(BSONDocument.empty).cursor().collect[List]().asAsyncEffect

        override def remove(id: String): F[Unit] = todos.findAndRemove(idFilter(id)).asAsyncEffect.void

        override def clear(): F[Unit] = todos.findAndRemove(BSONDocument.empty).asAsyncEffect.void
      }
    }
}
