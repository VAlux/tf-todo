package dev.alvo.todo.http.routes

import cats.effect.{Concurrent, ContextShift, Sync, Timer}
import cats.syntax.semigroupk._
import dev.alvo.todo.http.endpoints.TodoEndpoints._
import dev.alvo.todo.service.TodoService
import org.http4s.HttpRoutes
import sttp.tapir.server.http4s._

object TodoRoutes {

  def create[F[_]: Concurrent: ContextShift: Timer](todoService: TodoService[F])(implicit F: Sync[F]): F[Routes[F]] =
    F.delay {
      new Routes[F] {
        override val routes: HttpRoutes[F] =
          createTaskEndpoint.toRoutes(todoService.createTask) <+>
            getTaskByIdEndpoint.toRoutes(todoService.getTask) <+>
            getAllTasksEndpoint.toRoutes(_ => todoService.getAllTasks) <+>
            updateTaskEndpoint.toRoutes((todoService.updateTask _).tupled) <+>
            deleteTaskByIdEndpoint.toRoutes(todoService.removeTask) <+>
            deleteAllTasksEndpoint.toRoutes(_ => todoService.removeAllTasks())
      }
    }
}
