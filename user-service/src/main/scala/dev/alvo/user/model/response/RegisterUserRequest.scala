package dev.alvo.user.model.response

import io.circe.Codec
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import sttp.tapir.{Schema, Validator}

final case class RegisterUserRequest(email: String, name: String)

object RegisterUserRequest {
  implicit val createTaskRequestCodec: Codec[RegisterUserRequest] =
    Codec.from(deriveDecoder[RegisterUserRequest], deriveEncoder[RegisterUserRequest])

  implicit val createTaskRequestValidator: Validator[RegisterUserRequest] = Validator.derive

  implicit val createTaskRequestSchema: Schema[RegisterUserRequest] = Schema.derive
}
