package dev.alvo.todo.http.model.response

import cats.syntax.functor._
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}

sealed trait ErrorInfoResponse

sealed trait ServerErrorResponse extends ErrorInfoResponse
case class ServerErrorInfoResponse(message: String = "Internal Server Error") extends ServerErrorResponse

sealed trait UserErrorResponse extends ErrorInfoResponse
case class BadRequestResponse(what: String = "Bad Request") extends UserErrorResponse
case class NotFoundResponse(what: String = "Resource not found") extends UserErrorResponse

object ErrorInfoResponse {
  implicit val errorInfoResponseEncoder: Encoder[ErrorInfoResponse] = Encoder.instance {
    case server @ ServerErrorInfoResponse(_) => server.asJson
    case badRequest @ BadRequestResponse(_) => badRequest.asJson
    case notFound @ NotFoundResponse(_) => notFound.asJson
  }

  implicit val errorInfoResponseDecoder: Decoder[ErrorInfoResponse] =
    List[Decoder[ErrorInfoResponse]](
      Decoder[ServerErrorInfoResponse].widen,
      Decoder[BadRequestResponse].widen,
      Decoder[NotFoundResponse].widen
    ).reduceLeft(_ or _)
}
