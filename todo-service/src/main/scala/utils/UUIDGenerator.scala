package utils

import java.util.UUID

import cats.effect.Sync

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