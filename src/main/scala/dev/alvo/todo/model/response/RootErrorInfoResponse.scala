package dev.alvo.todo.model.response

import cats.syntax.functor._
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import sttp.tapir.{Schema, Validator}

sealed trait ErrorResponse

sealed trait ServerErrorResponse extends ErrorResponse
case class InternalErrorResponse(message: String = "Internal Server Error") extends ServerErrorResponse

sealed trait UserErrorResponse extends ErrorResponse
case class BadRequestResponse(what: String = "Bad Request") extends UserErrorResponse
case class NotFoundResponse(what: String = "Resource not found") extends UserErrorResponse
case class UnauthorizedResponse(message: String = s"No permissions to access the resource") extends UserErrorResponse

object ErrorResponse {
  implicit val errorInfoResponseEncoder: Encoder[ErrorResponse] = Encoder.instance {
    case server @ InternalErrorResponse(_) => server.asJson
    case badRequest @ BadRequestResponse(_) => badRequest.asJson
    case notFound @ NotFoundResponse(_) => notFound.asJson
    case unauthorized @ UnauthorizedResponse(_) => unauthorized.asJson
  }

  implicit val errorInfoResponseDecoder: Decoder[ErrorResponse] =
    List[Decoder[ErrorResponse]](
      Decoder[ServerErrorResponse].widen,
      Decoder[BadRequestResponse].widen,
      Decoder[NotFoundResponse].widen,
      Decoder[UnauthorizedResponse].widen
    ).reduceLeft(_ or _)

  implicit val errorInfoResponseValidator: Validator[ErrorResponse] = Validator.derive[ErrorResponse]

  implicit val errorInfoResponseSchema: Schema[ErrorResponse] = Schema.derive[ErrorResponse]
}
