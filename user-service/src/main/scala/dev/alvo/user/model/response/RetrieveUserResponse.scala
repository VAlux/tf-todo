package dev.alvo.user.model.response

import io.circe.Codec
import io.circe.generic.semiauto._
import sttp.tapir.{Schema, Validator}

final case class RetrieveUserResponse(email: String, name: String)

object RetrieveUserResponse {
  implicit val retrieveUserResponseValidator: Validator[RetrieveUserResponse] = Validator.derive

  implicit val retrieveUserResponseSchema: Schema[RetrieveUserResponse] = Schema.derive

  implicit val retrieveUserResponseCodec: Codec[RetrieveUserResponse] = Codec.from(deriveDecoder, deriveEncoder)
}
