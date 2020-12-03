package dev.alvo.todo.http.routes

import dev.alvo.todo.http.model.request.CreateTaskRequest
import dev.alvo.todo.http.model.request.CreateTaskRequest._
import dev.alvo.todo.http.model.response.RetrieveTaskResponse._
import dev.alvo.todo.http.model.response.{ErrorInfoResponse, RetrieveTaskResponse}
import sttp.tapir._
import sttp.tapir.json.circe.jsonBody

object TodoEndpoints {

  private val todoRoot: Endpoint[Unit, ErrorInfoResponse, Unit, Any] =
    RootEndpoint.rootV1.in("todo")

  val createTaskEndpoint: Endpoint[CreateTaskRequest, ErrorInfoResponse, RetrieveTaskResponse, Any] =
    todoRoot.post
      .description("Create new task")
      .in(jsonBody[CreateTaskRequest])
      .out(jsonBody[RetrieveTaskResponse])

  val updateTaskEndpoint: Endpoint[(String, CreateTaskRequest), ErrorInfoResponse, RetrieveTaskResponse, Any] =
    todoRoot
      .in(path[String]("todoId").description("Id of the task"))
      .put
      .description("Update existing task")
      .in(jsonBody[CreateTaskRequest])
      .out(jsonBody[RetrieveTaskResponse])

  val getAllTasksEndpoint: Endpoint[Unit, ErrorInfoResponse, List[RetrieveTaskResponse], Any] =
    todoRoot.get
      .description("Retrieve all of the available tasks")
      .out(jsonBody[List[RetrieveTaskResponse]])

  val getTaskByIdEndpoint: Endpoint[String, ErrorInfoResponse, RetrieveTaskResponse, Any] =
    todoRoot
      .in(path[String]("todoId").description("Id of the task"))
      .get
      .description("Get specific task by id")
      .out(jsonBody[RetrieveTaskResponse])

  val deleteTaskByIdEndpoint: Endpoint[String, ErrorInfoResponse, String, Any] =
    todoRoot
      .in(path[String]("todoId").description("Id of the task"))
      .delete
      .description("Delete specific task by id")
      .out(jsonBody[String])

  val deleteAllTasksEndpoint: Endpoint[Unit, ErrorInfoResponse, String, Any] =
    todoRoot.delete
      .description("Delete all available tasks")
      .out(jsonBody[String])
}
