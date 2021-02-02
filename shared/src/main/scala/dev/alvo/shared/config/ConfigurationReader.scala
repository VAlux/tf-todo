package dev.alvo.shared.config

import cats.effect.Sync
import pureconfig._
import pureconfig.error.ConfigReaderFailures

trait ConfigurationReader[F[_], C] {
  def loadConfiguration(configSource: ConfigSource): F[C]
}

object ConfigurationReader {
  import cats.syntax.flatMap._
  import cats.syntax.functor._

  def apply[F[_], C](implicit F: Sync[F], loader: ConfigurationLoader[C]): F[ConfigurationReader[F, C]] = F.delay {
    (configSource: ConfigSource) =>
      F.delay(loader.loadConfig(configSource))
        .flatMap(_.fold(error => processConfigurationLoadingError[F, C](error, loader.default), F.pure[C]))
  }

  def processConfigurationLoadingError[F[_], C](error: ConfigReaderFailures, default: C)(implicit F: Sync[F]): F[C] =
    for {
      _ <- F.delay(Console.err.println("Error reading configuration:"))
      _ <- F.delay(Console.err.println(error.prettyPrint()))
      _ <- F.delay(Console.err.println("Falling back to the default one..."))
    } yield default
}
