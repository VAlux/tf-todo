package dev.alvo.todo.http.routes

import cats.effect.{Concurrent, ContextShift, Timer}
import cats.syntax.semigroupk._
import dev.alvo.todo.http.routes.TodoEndpoints._
import dev.alvo.todo.service.TodoService
import org.http4s.HttpRoutes
import sttp.tapir.server.http4s._

class TodoRoutes[F[_]: Concurrent: ContextShift: Timer](todoService: TodoService[F]) {

  val todoServiceRoutes: HttpRoutes[F] =
    deleteAllTasksEndpoint.toRoutes(_ => todoService.removeAllTasks()) <+>
      createTaskEndpoint.toRoutes(todoService.createTask) <+>
      deleteTaskByIdEndpoint.toRoutes(todoService.removeTask) <+>
      getTaskByIdEndpoint.toRoutes(todoService.getTask) <+>
      getAllTasksEndpoint.toRoutes(_ => todoService.getAllTasks) <+>
      updateTaskEndpoint.toRoutes((todoService.updateTask _).tupled)
}
