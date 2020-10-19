package dev.alvo.todo.storage.model

import dev.alvo.todo.storage.model.TasksModel.TaskWithId
import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

final case class TasksModel(tasks: Seq[TaskWithId])

object TasksModel {
  type TaskWithId = (String, Task)

  implicit val tasksModelEncoder: Encoder[TasksModel] = deriveEncoder

  implicit def tasksModelEntityEncoder[F[_]]: EntityEncoder[F, TasksModel] = jsonEncoderOf
}
