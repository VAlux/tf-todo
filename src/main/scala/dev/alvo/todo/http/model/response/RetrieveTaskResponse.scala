package dev.alvo.todo.http.model.response

import cats.effect.Sync
import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

final case class RetrieveTaskResponse(id: String, action: String)

object RetrieveTaskResponse {
  implicit val retrieveTaskResponseEncoder: Encoder[RetrieveTaskResponse] = deriveEncoder

  implicit def retrieveTaskResponseEntityEncoder[F[_]: Sync]: EntityEncoder[F, RetrieveTaskResponse] = jsonEncoderOf

  implicit def retrieveTaskResponseEntityEncoderList[F[_]: Sync]: EntityEncoder[F, List[RetrieveTaskResponse]] =
    jsonEncoderOf
}
