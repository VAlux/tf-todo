package dev.alvo.todo.model.response

import io.circe.Codec
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import sttp.tapir.{Schema, Validator}

final case class RetrieveTaskResponse(id: String, action: String)

object RetrieveTaskResponse {
  implicit val retrieveTaskResponseValidator: Validator[RetrieveTaskResponse] = Validator.derive

  implicit val retrieveTaskResponseSchema: Schema[RetrieveTaskResponse] = Schema.derive

  implicit val retrieveTaskResponseCodec: Codec[RetrieveTaskResponse] =
    Codec.from(deriveDecoder[RetrieveTaskResponse], deriveEncoder[RetrieveTaskResponse])
}
