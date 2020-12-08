package dev.alvo.todo.http.controller

import cats.effect.{Concurrent, ContextShift, Timer}
import cats.syntax.functor._
import dev.alvo.todo.http.endpoints.TodoEndpoints
import dev.alvo.todo.http.routes.TodoRoutes

object TodoController {
  def create[F[_]: Concurrent: ContextShift: Timer](endpoints: TodoEndpoints[F]): F[Controller[F]] =
    TodoRoutes.create(endpoints).map(todo => Controller.fromRoutes(todo.routes))
}
