package dev.alvo.user.endpoints.application

import sttp.tapir.Endpoint
import sttp.tapir.server.ServerEndpoint

trait ApplicationEndpoints[F[_]] {
  def asSeq(): Seq[ServerEndpoint[_, _, _, _, F]]
}
