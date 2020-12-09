package dev.alvo.todo.endpoints

import sttp.tapir.docs.openapi.RichOpenAPIServerEndpoints
import sttp.tapir.openapi.OpenAPI

class OpenApiEndpoints[F[_]](todoEndpoints: TodoEndpoints[F]) {

  val endpoints: OpenAPI =
    Seq(
      todoEndpoints.createTask,
      todoEndpoints.updateTask,
      todoEndpoints.getTaskById,
      todoEndpoints.getAllTasks,
      todoEndpoints.deleteTaskById,
      todoEndpoints.deleteAllTasks
    ).toOpenAPI("ToDo API", "1.0")
}
