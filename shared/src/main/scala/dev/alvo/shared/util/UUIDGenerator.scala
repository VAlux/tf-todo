package dev.alvo.shared.util

import cats.effect.Sync

import java.util.UUID

trait UUIDGenerator[F[_]] {
  def generate: F[UUID]
}

object UUIDGenerator {
  def apply[F[_]](implicit F: Sync[F]): F[UUIDGenerator[F]] = F.delay {
    new UUIDGenerator[F] {
      override def generate: F[UUID] = F.delay(UUID.randomUUID())
    }
  }
}
