package dev.alvo.todo.http.model.request

import cats.effect.Sync
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf

final case class CreateTaskRequest(action: String)

object CreateTaskRequest {
  implicit val createTaskRequestDecoder: Decoder[CreateTaskRequest] = deriveDecoder

  implicit def createTaskRequestEntityDecoder[F[_]: Sync]: EntityDecoder[F, CreateTaskRequest] = jsonOf
}
