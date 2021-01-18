package dev.alvo.todo.config

import cats.effect.Sync
import pureconfig._
import pureconfig.error.ConfigReaderFailures
import pureconfig.generic.ProductHint

trait ConfigurationReader[F[_]] {
  def loadConfiguration(configSource: ConfigSource): F[Configuration]
}

object ConfigurationReader {
  import pureconfig.generic.auto._
  import cats.syntax.flatMap._
  import cats.syntax.functor._

  implicit private[this] def hint[A]: ProductHint[A] = ProductHint[A](ConfigFieldMapping(CamelCase, CamelCase))

  def create[F[_]](implicit F: Sync[F]): F[ConfigurationReader[F]] = F.delay { (configSource: ConfigSource) =>
    F.delay(configSource.load[Configuration]).flatMap(_.fold(processConfigurationLoadingError[F], F.pure))
  }

  def processConfigurationLoadingError[F[_]](error: ConfigReaderFailures)(implicit F: Sync[F]): F[Configuration] =
    for {
      _ <- F.delay(Console.err.println("Error reading configuration:"))
      _ <- F.delay(Console.err.println(error.prettyPrint()))
      _ <- F.delay(Console.err.println("Falling back to the default one..."))
    } yield Configuration.default
}
