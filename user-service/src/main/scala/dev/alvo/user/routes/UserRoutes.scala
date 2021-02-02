package dev.alvo.user.routes

import cats.effect.{Concurrent, ContextShift, Sync, Timer}
import dev.alvo.user.endpoints.application.UserEndpoints
import org.http4s.HttpRoutes
import sttp.tapir.server.http4s.{RichHttp4sHttpEndpoint, RichHttp4sServerEndpoint}

object UserRoutes {
  import cats.syntax.semigroupk._

  def apply[F[_]: Concurrent: ContextShift: Timer](userEndpoints: UserEndpoints[F])(implicit F: Sync[F]): F[Routes[F]] =
    F.delay {
      new Routes[F] {
        override val routes: HttpRoutes[F] =
          userEndpoints.findUser.toRoutes <+>
            userEndpoints.registerUser.toRoutes <+>
            userEndpoints.disableUser.toRoutes
      }
    }
}
