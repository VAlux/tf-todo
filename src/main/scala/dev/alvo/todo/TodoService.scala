package dev.alvo.todo

import java.util.UUID

import cats.Functor
import cats.effect.Sync
import cats.effect.concurrent.Ref
import cats.implicits._
import dev.alvo.todo.TodoStorage.{Task, TasksModel}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}

trait TodoStorage[F[_]] {
  def add(task: Task): F[Unit]

  def get(id: String): F[Option[Task]]

  def getAll: F[TasksModel]

  def remove(id: String): F[Unit]

  def clear(): F[Unit]
}

class InMemoryTodoStorage[F[_] : Functor](private val storage: Ref[F, Map[String, Task]]) extends TodoStorage[F] {
  override def add(task: Task): F[Unit] = storage.update(s => s.updated(UUID.randomUUID().toString, task))

  override def get(id: String): F[Option[Task]] = storage.get.map(_.get(id))

  override def remove(id: String): F[Unit] = storage.update(s => s - id)

  override def clear(): F[Unit] = storage.update(_ => Map.empty)

  override def getAll: F[TasksModel] = storage.get.map(s => TasksModel(s.values.toSeq))
}

object TodoStorage {

  implicit def apply[F[_]](implicit ev: TodoStorage[F]): TodoStorage[F] = ev

  final case class Task(id: String, action: String)

  object Task {
    implicit val taskDecoder: Decoder[Task] = deriveDecoder
    implicit def taskEntityDecoder[F[_] : Sync]: EntityDecoder[F, Task] = jsonOf

    implicit val taskEncoder: Encoder[Task] = deriveEncoder
    implicit def taskEntityEncoder[F[_] : Sync]: EntityEncoder[F, Task] = jsonEncoderOf
  }

  final case class TasksModel(tasks: Seq[Task])

  object TasksModel {
    implicit val tasksModelEncoder: Encoder[TasksModel] = deriveEncoder[TasksModel]
    implicit def tasksModelEntityEncoder[F[_]]: EntityEncoder[F, TasksModel] = jsonEncoderOf[F, TasksModel]
  }

  def impl[F[_] : Functor](storage: Ref[F, Map[String, Task]]): TodoStorage[F] =
    new InMemoryTodoStorage[F](storage)
}