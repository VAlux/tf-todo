package dev.alvo.user

import cats.effect.{ConcurrentEffect, ContextShift, ExitCode, IO, IOApp, Timer}
import cats.syntax.flatMap._
import dev.alvo.shared.config.ConfigurationReader
import dev.alvo.user.application.UserHttpApplication
import dev.alvo.user.config.UserConfiguration
import fs2.Stream
import org.http4s.HttpApp
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import pureconfig.ConfigSource
import pureconfig.error.ConfigReaderFailures

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext.global
import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService}

object Main extends IOApp {

  type FStream[F[_]] = Stream[F, ExitCode]

  private val dbExecutionContext: ExecutionContextExecutorService =
    ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(4))

  def run(args: List[String]): IO[ExitCode] =
    for {
      config <- ConfigurationReader[IO, ConfigSource, UserConfiguration, ConfigReaderFailures].flatMap(
        _.loadConfiguration(ConfigSource.default)
      )
      app <- createApplication[IO](config)
      server <- stream[IO](config, app).compile.drain.as(ExitCode.Success)
    } yield server

  private def stream[F[_]: ConcurrentEffect: Timer: ContextShift](
    conf: UserConfiguration,
    app: HttpApp[F]
  ): FStream[F] =
    for {
      finalHttpApp <- Stream(Logger.httpApp(logHeaders = conf.log.logHeaders, logBody = conf.log.logBody)(app))
      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(conf.http.port, conf.http.host)
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode

  private def createApplication[F[_]: ConcurrentEffect: ContextShift: Timer](config: UserConfiguration): F[HttpApp[F]] =
    UserHttpApplication[F](dbExecutionContext).flatMap(_.createEntrypoint(config))
}
