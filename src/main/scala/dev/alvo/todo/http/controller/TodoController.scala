package dev.alvo.todo.http.controller

import cats.effect.{Concurrent, ContextShift, Timer}
import cats.syntax.functor._
import dev.alvo.todo.http.routes.TodoRoutes
import dev.alvo.todo.service.TodoService

object TodoController {
  def create[F[_]: Concurrent: ContextShift: Timer](service: TodoService[F]): F[Controller[F]] =
    TodoRoutes.create(service).map(todoRoutes => Controller.fromRoutes(todoRoutes.routes))
}
