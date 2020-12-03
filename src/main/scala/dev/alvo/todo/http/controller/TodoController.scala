package dev.alvo.todo.http.controller

import cats.effect.{Concurrent, ContextShift, Timer}
import cats.syntax.functor._
import dev.alvo.todo.http.routes.TodoRoutes
import dev.alvo.todo.service.TodoService
import org.http4s.HttpRoutes

object TodoController {

  def create[F[_]: Concurrent: ContextShift: Timer](service: TodoService[F]): F[Controller[F]] =
    for {
      todoRoutes <- TodoRoutes.create(service)
      controller = new Controller[F] {
        override val routes: HttpRoutes[F] = todoRoutes.routes
      }
    } yield controller
}
