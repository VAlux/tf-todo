package dev.alvo.todo.storage

import cats.effect.{Async, ContextShift}
import cats.implicits._
import dev.alvo.todo.database.MongoDb
import dev.alvo.todo.storage.model.Task
import org.bson.BsonDocument
import org.mongodb.scala.Observable
import org.mongodb.scala.model.Filters
import utils.UUIDGenerator

object MongodbTodoStorage {

  def apply[F[_]: ContextShift](mongoDsl: MongoDb[F], uuid: UUIDGenerator[F])(implicit F: Async[F]): F[TodoStorage[F]] =
    mongoDsl.getDatabase("todo").map(_.getCollection[Task.Existing]("todos")).map { collection =>
      new TodoStorage[F] {
        override def add(task: Task.New): F[Option[Task.Existing]] =
          for {
            id <- uuid.generate
            _ <- collection.insertOne(Task.Existing(id.toString, task.action)).asDelayed
            added <- get(id.toString)
          } yield added

        override def get(id: String): F[Option[Task.Existing]] =
          collection.find(Filters.eq("id", id)).asDelayedOption

        override def update(id: String, task: Task.New): F[Option[Task.Existing]] =
          collection.replaceOne(Filters.eq("id", id), Task.Existing(id, task.action)).asDelayed >> get(id)

        override def getAll: F[List[Task.Existing]] = collection.find().collect().asDelayed.map(_.toList)

        override def remove(id: String): F[Unit] = collection.deleteOne(Filters.eq("id", id)).asDelayed.void

        override def clear(): F[Unit] = collection.deleteMany(BsonDocument.parse("{}")).asDelayed.void
      }
    }

  implicit private class ObservableOps[A](val observable: Observable[A]) extends AnyVal {
    def asDelayed[F[_]: ContextShift](implicit F: Async[F]): F[A] =
      Async.fromFuture(F.delay(observable.head()))

    def asDelayedOption[F[_]: ContextShift](implicit F: Async[F]): F[Option[A]] =
      Async.fromFuture(F.delay(observable.headOption()))
  }
}
