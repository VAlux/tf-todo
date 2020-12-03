package dev.alvo.todo.http.endpoints

import dev.alvo.todo.http.endpoints.TodoEndpoints._
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.OpenAPI

object OpenApiEndpoints {

  val openApiEndpoints: OpenAPI = Seq(
    createTaskEndpoint,
    updateTaskEndpoint,
    getTaskByIdEndpoint,
    getAllTasksEndpoint,
    deleteTaskByIdEndpoint,
    deleteAllTasksEndpoint
  ).toOpenAPI("ToDo API", "1.0")
}
