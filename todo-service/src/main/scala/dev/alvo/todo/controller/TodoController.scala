package dev.alvo.todo.controller

import cats.effect.{Concurrent, ContextShift, Timer}
import dev.alvo.todo.endpoints.application.TodoEndpoints
import dev.alvo.todo.routes.TodoRoutes

object TodoController {
  import cats.syntax.functor._

  def apply[F[_]: Concurrent: ContextShift: Timer](endpoints: TodoEndpoints[F]): F[Controller[F]] =
    TodoRoutes.create(endpoints).map(todo => Controller.fromRoutes(todo.routes))
}
