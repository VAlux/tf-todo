package dev.alvo.user.model.response

import io.circe.Codec
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import sttp.tapir.{Schema, Validator}

final case class RegisterUserResponse(email: String, name: String)

object RegisterUserResponse {

  implicit val createTaskRequestCodec: Codec[RegisterUserResponse] =
    Codec.from(deriveDecoder[RegisterUserResponse], deriveEncoder[RegisterUserResponse])

  implicit val createTaskRequestValidator: Validator[RegisterUserResponse] = Validator.derive

  implicit val createTaskRequestSchema: Schema[RegisterUserResponse] = Schema.derive
}
