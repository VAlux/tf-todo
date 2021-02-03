package dev.alvo.shared.config

import cats.effect.Sync

trait ConfigurationReader[F[_], S, C] {
  def loadConfiguration(configSource: S): F[C]
}

object ConfigurationReader {
  import cats.syntax.flatMap._
  import cats.syntax.functor._

  /** Load configuration using the implicit configuration loader for the specified configuration source type
    * @param F Effect type
    * @param loader Configuration loader for configuration type C
    * @tparam F Sync effect context bound
    * @tparam S Configuration loading source type
    * @tparam C Configuration model type
    * @tparam E Configuration loading error type
    * @return Configuration reader creation effect
    */
  def apply[F[_], S, C, E](implicit F: Sync[F], loader: ConfigurationLoader[S, C, E]): F[ConfigurationReader[F, S, C]] =
    F.delay { (configSource: S) =>
      F.delay(loader.loadConfig(configSource))
        .flatMap(
          _.fold(
            error => processConfigurationLoadingError[F, C, E](loader.formatError(error), loader.default),
            F.pure[C]
          )
        )
    }

  private def processConfigurationLoadingError[F[_], C, E](error: String, default: C)(implicit F: Sync[F]): F[C] =
    for {
      _ <- F.delay(Console.err.println("Error reading configuration:"))
      _ <- F.delay(Console.err.println(error))
      _ <- F.delay(Console.err.println("Falling back to the default one..."))
    } yield default
}
