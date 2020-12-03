package dev.alvo.todo.http.controller

import cats.effect.{Concurrent, ContextShift, Sync, Timer}
import dev.alvo.todo.http.routes.TodoRoutes
import dev.alvo.todo.service.TodoService
import org.http4s.HttpRoutes

object TodoController {

  def create[F[_]: Concurrent: ContextShift: Timer](service: TodoService[F])(implicit F: Sync[F]): F[Controller[F]] =
    F.delay {
      new Controller[F] {
        override val routes: HttpRoutes[F] = new TodoRoutes[F](service).todoServiceRoutes
      }
    }
}
