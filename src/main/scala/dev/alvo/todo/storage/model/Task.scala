package dev.alvo.todo.storage.model

import cats.effect.Sync
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}

final case class Task(id: String, action: String)

object Task {
  implicit val taskDecoder: Decoder[Task] = deriveDecoder

  implicit def taskEntityDecoder[F[_]: Sync]: EntityDecoder[F, Task] = jsonOf

  implicit val taskEncoder: Encoder[Task] = deriveEncoder

  implicit def taskEntityEncoder[F[_]: Sync]: EntityEncoder[F, Task] = jsonEncoderOf
}
