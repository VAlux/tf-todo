package dev.alvo.todo.config

import cats.effect.Sync
import pureconfig._
import pureconfig.generic.ProductHint
import pureconfig.generic.auto._

trait ConfigurationReader[F[_]] {
  def loadConfiguration: F[ConfigReader.Result[Configuration]]
}

object ConfigurationReader {
  implicit private[this] def hint[A]: ProductHint[A] = ProductHint[A](ConfigFieldMapping(CamelCase, CamelCase))

  def apply[F[_]](implicit ev: F[ConfigurationReader[F]]): F[ConfigurationReader[F]] = ev

  implicit def dsl[F[_]](implicit F: Sync[F]): F[ConfigurationReader[F]] = F.delay {
    new ConfigurationReader[F] {
      override def loadConfiguration: F[ConfigReader.Result[Configuration]] = F.delay(ConfigSource.default.load)
    }
  }
}
