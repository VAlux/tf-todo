package dev.alvo.todo.http.controller

import cats.effect.Sync
import dev.alvo.todo.http.routes.TodoRoutes
import dev.alvo.todo.storage.TodoStorage
import org.http4s.HttpRoutes
import org.http4s.server.Router

object TodoController {

  def create[F[_]](storage: TodoStorage[F])(implicit F: Sync[F]): F[Controller[F]] = F.delay {
    new Controller[F] {
      override val routes: HttpRoutes[F] =
        Router("todo" -> new TodoRoutes[F](storage).todoServiceRoutes)
    }
  }
}
