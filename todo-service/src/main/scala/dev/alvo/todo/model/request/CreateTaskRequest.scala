package dev.alvo.todo.model.request

import io.circe.Codec
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import sttp.tapir.{Schema, Validator}

final case class CreateTaskRequest(action: String)

object CreateTaskRequest {
  implicit val createTaskRequestCodec: Codec[CreateTaskRequest] =
    Codec.from(deriveDecoder[CreateTaskRequest], deriveEncoder[CreateTaskRequest])

  implicit val createTaskRequestValidator: Validator[CreateTaskRequest] = Validator.derive

  implicit val createTaskRequestSchema: Schema[CreateTaskRequest] = Schema.derive
}
