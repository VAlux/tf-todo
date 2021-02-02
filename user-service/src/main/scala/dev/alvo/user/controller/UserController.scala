package dev.alvo.user.controller

import cats.effect.{Concurrent, ContextShift, Timer}
import dev.alvo.user.endpoints.application.UserEndpoints
import dev.alvo.user.routes.UserRoutes

object UserController {
  import cats.syntax.functor._

  def apply[F[_]: Concurrent: ContextShift: Timer](endpoints: UserEndpoints[F]): F[Controller[F]] =
    UserRoutes(endpoints).map(userRoutes => Controller.fromRoutes(userRoutes.routes))
}
