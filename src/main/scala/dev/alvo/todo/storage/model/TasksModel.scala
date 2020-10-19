package dev.alvo.todo.storage.model

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

final case class TasksModel(tasks: Seq[Task])

object TasksModel {
  implicit val tasksModelEncoder: Encoder[TasksModel] = deriveEncoder

  implicit def tasksModelEntityEncoder[F[_]]: EntityEncoder[F, TasksModel] = jsonEncoderOf
}
