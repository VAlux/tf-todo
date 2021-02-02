package dev.alvo.user.model.response

sealed trait ErrorResponse

object ErrorResponse {
  sealed trait ServerErrorResponse extends ErrorResponse

  object ServerErrorResponse {
    case class InternalErrorResponse(message: String = "Internal Server Error") extends ServerErrorResponse
  }

  sealed trait UserErrorResponse extends ErrorResponse

  object UserErrorResponse {
    case class BadRequestResponse(what: String = "Bad Request") extends UserErrorResponse
    case class NotFoundResponse(what: String = "Resource not found") extends UserErrorResponse
    case class UnauthorizedResponse(message: String = s"No permissions to access the resource")
        extends UserErrorResponse
  }
}
