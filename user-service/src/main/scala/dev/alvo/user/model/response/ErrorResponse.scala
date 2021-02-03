package dev.alvo.user.model.response

sealed trait ErrorResponse

object ErrorResponse {
  sealed trait ServerErrorResponse extends ErrorResponse

  object ServerErrorResponse {
    case class InternalErrorResponse(message: String = "Internal Server Error") extends ServerErrorResponse
  }

  sealed trait UserErrorResponse extends ErrorResponse

  object UserErrorResponse {
    case class BadRequestResponse(message: String = "Bad Request") extends UserErrorResponse

    case class NotFoundResponse(message: String = "Resource not found") extends UserErrorResponse

    case class UserNotRegisteredResponse(message: String = "User registration failure!") extends UserErrorResponse

    case class UnauthorizedResponse(message: String = "No permissions to access the resource") extends UserErrorResponse
  }
}
