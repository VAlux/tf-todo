package dev.alvo.todo.routes

import cats.effect.{Concurrent, ContextShift, Sync, Timer}
import cats.implicits.toSemigroupKOps
import dev.alvo.todo.endpoints.application.TodoEndpoints
import org.http4s.HttpRoutes
import sttp.tapir.server.http4s.RichHttp4sServerEndpoint

object TodoRoutes {
  def create[F[_]: Concurrent: ContextShift: Timer](todo: TodoEndpoints[F])(implicit F: Sync[F]): F[Routes[F]] =
    F.delay {
      new Routes[F] {
        override val routes: HttpRoutes[F] =
          todo.createTask.toRoutes <+>
            todo.getTaskById.toRoutes <+>
            todo.getAllTasks.toRoutes <+>
            todo.updateTask.toRoutes <+>
            todo.deleteTaskById.toRoutes <+>
            todo.deleteAllTasks.toRoutes
      }
    }
}
