package dev.alvo.user.endpoints

import dev.alvo.user.model.response.ErrorResponse.ServerErrorResponse.InternalErrorResponse
import dev.alvo.user.model.response.ErrorResponse.UserErrorResponse.{
  BadRequestResponse,
  NotFoundResponse,
  UnauthorizedResponse
}
import dev.alvo.user.model.response._
import io.circe.generic.auto._
import sttp.model.StatusCode
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.{endpoint, _}

object RootEndpoint {

  val rootV1: Endpoint[Unit, ErrorResponse, Unit, Any] =
    endpoint
      .description("Main entry point of the user-service API")
      .name("user-service API")
      .in("api" / "v1")
      .errorOut(errorResponseMapping)

  private lazy val errorResponseMapping: EndpointOutput.OneOf[ErrorResponse, ErrorResponse] =
    oneOf[ErrorResponse](
      statusMapping(
        StatusCode.NotFound,
        jsonBody[NotFoundResponse].description("Not found")
      ),
      statusMapping(
        StatusCode.BadRequest,
        jsonBody[BadRequestResponse].description("Bad request")
      ),
      statusMapping(
        StatusCode.Unauthorized,
        jsonBody[UnauthorizedResponse].description("Unauthorized")
      ),
      statusMapping(
        StatusCode.InternalServerError,
        jsonBody[InternalErrorResponse].description("Internal server error")
      )
    )
}
