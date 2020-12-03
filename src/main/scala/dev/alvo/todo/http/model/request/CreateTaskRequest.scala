package dev.alvo.todo.http.model.request

import cats.effect.Sync
import io.circe.Codec
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf
import sttp.tapir.{Schema, Validator}

final case class CreateTaskRequest(action: String)

object CreateTaskRequest {
  implicit val createTaskRequestCodec: Codec[CreateTaskRequest] =
    Codec.from(deriveDecoder[CreateTaskRequest], deriveEncoder[CreateTaskRequest])

  implicit val createTaskRequestValidator: Validator[CreateTaskRequest] = Validator.derive

  implicit val createTaskRequestSchema: Schema[CreateTaskRequest] = Schema.derive

  implicit def createTaskRequestEntityDecoder[F[_]: Sync]: EntityDecoder[F, CreateTaskRequest] = jsonOf
}
