package dev.alvo.todo.endpoints

import cats.effect.Sync
import dev.alvo.todo.model.User
import dev.alvo.todo.model.request.CreateTaskRequest
import dev.alvo.todo.model.response.{ErrorResponse, RetrieveTaskResponse}
import dev.alvo.todo.service.{AuthenticationService, TodoService}
import sttp.tapir._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.PartialServerEndpoint
import cats.syntax.all._

import scala.language.existentials

class TodoEndpoints[F[_]: Sync](todoService: TodoService[F], authenticationService: AuthenticationService[F]) {

  private val todoRoot: PartialServerEndpoint[User, Unit, ErrorResponse, Unit, Any, F] =
    RootEndpoint.secureRootV1[F](authenticationService).in("todo")

  val createTask =
    todoRoot.post
      .description("Create new task")
      .in(jsonBody[CreateTaskRequest])
      .out(jsonBody[RetrieveTaskResponse])
      .serverLogic {
        case (_: User, request: CreateTaskRequest) => todoService.createTask(request)
      }

  val updateTask =
    todoRoot
      .in(path[String]("todoId").description("Id of the task"))
      .put
      .description("Update existing task")
      .in(jsonBody[CreateTaskRequest])
      .out(jsonBody[RetrieveTaskResponse])
      .serverLogic {
        case (_: User, (id: String, request: CreateTaskRequest)) => todoService.updateTask(id, request)
      }

  val getAllTasks =
    todoRoot.get
      .description("Retrieve all of the available tasks")
      .out(jsonBody[List[RetrieveTaskResponse]])
      .serverLogic(_ => todoService.getAllTasks)

  val getTaskById =
    todoRoot
      .in(path[String]("todoId").description("Id of the task"))
      .get
      .description("Get specific task by id")
      .out(jsonBody[RetrieveTaskResponse])
      .serverLogic { case (_: User, id: String) => todoService.getTask(id) }

  val deleteTaskById =
    todoRoot
      .in(path[String]("todoId").description("Id of the task"))
      .delete
      .description("Delete specific task by id")
      .out(jsonBody[String])
      .serverLogic { case (_: User, id: String) => todoService.removeTask(id) }

  val deleteAllTasks =
    todoRoot.delete
      .description("Delete all available tasks")
      .out(jsonBody[String])
      .serverLogic(_ => todoService.removeAllTasks())
}
