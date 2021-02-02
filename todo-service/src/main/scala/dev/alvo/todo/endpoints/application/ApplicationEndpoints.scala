package dev.alvo.todo.endpoints.application

import sttp.tapir.server.ServerEndpoint

trait ApplicationEndpoints[F[_]] {
  def asSeq(): Seq[ServerEndpoint[_, _, _, _, F]]
}
