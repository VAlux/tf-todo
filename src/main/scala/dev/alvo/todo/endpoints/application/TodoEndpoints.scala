package dev.alvo.todo.endpoints.application

import cats.effect.Sync
import cats.implicits._
import dev.alvo.todo.endpoints.RootEndpoint
import dev.alvo.todo.model.User
import dev.alvo.todo.model.request.CreateTaskRequest
import dev.alvo.todo.model.response.{ErrorResponse, NotFoundResponse, RetrieveTaskResponse}
import dev.alvo.todo.service.TodoService
import dev.alvo.todo.service.authentication.JwtAuthenticationService
import dev.alvo.todo.storage.model.Task
import sttp.tapir._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.{PartialServerEndpoint, ServerEndpoint}

import scala.language.existentials

class TodoEndpoints[F[_]: Sync](todoService: TodoService[F], authenticationService: JwtAuthenticationService[F])
    extends ApplicationEndpoints[F] {

  private val todoRoot: PartialServerEndpoint[User, Unit, ErrorResponse, Unit, Any, F] =
    RootEndpoint.secureRootV1[F](authenticationService).in("todo")

  val createTask =
    todoRoot.post
      .description("Create new task")
      .in(jsonBody[CreateTaskRequest])
      .out(jsonBody[RetrieveTaskResponse])
      .serverLogic {
        case (_: User, request: CreateTaskRequest) =>
          todoService.createTask(request).map(taskOptionToResponse)
      }

  val getTaskById =
    todoRoot
      .in(path[String]("todoId").description("Id of the task"))
      .get
      .description("Get specific task by id")
      .out(jsonBody[RetrieveTaskResponse])
      .serverLogic {
        case (_: User, id: String) =>
          todoService.getTask(id).map(taskOptionToResponse)
      }

  val getAllTasks =
    todoRoot.get
      .description("Retrieve all of the available tasks")
      .out(jsonBody[List[RetrieveTaskResponse]])
      .serverLogic { _ =>
        for {
          tasks <- todoService.getAllTasks
          response = tasks.map(task => RetrieveTaskResponse(task.id, task.action))
        } yield Right[ErrorResponse, List[RetrieveTaskResponse]](response)
      }

  val updateTask =
    todoRoot
      .in(path[String]("todoId").description("Id of the task"))
      .put
      .description("Update existing task")
      .in(jsonBody[CreateTaskRequest])
      .out(jsonBody[RetrieveTaskResponse])
      .serverLogic {
        case (_: User, (id: String, request: CreateTaskRequest)) =>
          todoService.updateTask(id, request).map(taskOptionToResponse)
      }

  val deleteTaskById =
    todoRoot
      .in(path[String]("todoId").description("Id of the task"))
      .delete
      .description("Delete specific task by id")
      .out(jsonBody[String])
      .serverLogic {
        case (_: User, id: String) => todoService.removeTask(id).map(Right[ErrorResponse, String])
      }

  val deleteAllTasks =
    todoRoot.delete
      .description("Delete all available tasks")
      .out(jsonBody[String])
      .serverLogic(_ => todoService.removeAllTasks().map(Right[ErrorResponse, String]))

  private def taskOptionToResponse(task: Option[Task.Existing]): Either[ErrorResponse, RetrieveTaskResponse] =
    task.fold[Either[ErrorResponse, RetrieveTaskResponse]](Left(NotFoundResponse()))(
      task => Right(RetrieveTaskResponse(task.id, task.action))
    )

  override def asSeq(): Seq[ServerEndpoint[_, _, _, _, F]] =
    Seq(createTask, getTaskById, getAllTasks, updateTask, deleteTaskById, deleteAllTasks)
}
