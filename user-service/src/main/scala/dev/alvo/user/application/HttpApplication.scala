package dev.alvo.user.application

import dev.alvo.user.config.UserConfiguration
import org.http4s.HttpApp

trait HttpApplication[F[_]] {
  def createEntrypoint(config: UserConfiguration): F[HttpApp[F]]
}
