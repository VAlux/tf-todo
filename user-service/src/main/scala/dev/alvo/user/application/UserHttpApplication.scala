package dev.alvo.user.application
import cats.effect.{Async, ConcurrentEffect, ContextShift, Sync, Timer}
import dev.alvo.mongodb.MongoDb
import dev.alvo.user.Entrypoint
import dev.alvo.user.config.UserConfiguration
import dev.alvo.user.controller.UserController
import dev.alvo.user.endpoints.application.UserEndpoints
import dev.alvo.user.repository.UserMongoRepository
import dev.alvo.user.service.UserService

import scala.concurrent.ExecutionContext

object UserHttpApplication {
  import cats.syntax.flatMap._
  import cats.syntax.functor._

  def apply[F[_]: ConcurrentEffect: ContextShift: Timer](
    ec: ExecutionContext
  )(implicit F: Sync[F]): F[HttpApplication[F]] =
    F.delay { (config: UserConfiguration) =>
      for {
        storage <- MongoDb(config.mongo)(implicitly[ContextShift[F]], implicitly[Async[F]], ec)
        repository <- UserMongoRepository(storage)(implicitly[ContextShift[F]], implicitly[Async[F]], ec)
        userService <- UserService(repository)
        endpoints = new UserEndpoints[F](userService)
        userController <- UserController(endpoints)
      } yield Entrypoint.forControllers(userController)
    }
}
