package dev.alvo.todo.http.model.response

import cats.effect.Sync
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Codec, Decoder, Encoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}
import sttp.tapir.{Schema, Validator}

final case class RetrieveTaskResponse(id: String, action: String)

object RetrieveTaskResponse {
  implicit def retrieveTaskResponseEntityEncoder[F[_]: Sync]: EntityEncoder[F, RetrieveTaskResponse] = jsonEncoderOf

  implicit def retrieveTaskResponseEntityEncoderList[F[_]: Sync]: EntityEncoder[F, List[RetrieveTaskResponse]] =
    jsonEncoderOf

  implicit def retrieveTaskResponseEntityDecoder[F[_]: Sync]: EntityDecoder[F, RetrieveTaskResponse] = jsonOf

  implicit def retrieveTaskResponseEntityDecoderList[F[_]: Sync]: EntityDecoder[F, List[RetrieveTaskResponse]] = jsonOf

  implicit val retrieveTaskResponseValidator: Validator[RetrieveTaskResponse] = Validator.derive

  implicit val retrieveTaskResponseSchema: Schema[RetrieveTaskResponse] = Schema.derive

  implicit val retrieveTaskResponseCodec: Codec[RetrieveTaskResponse] =
    Codec.from(deriveDecoder[RetrieveTaskResponse], deriveEncoder[RetrieveTaskResponse])
}
