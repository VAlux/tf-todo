package dev.alvo.todo

import cats.effect.{ConcurrentEffect, ContextShift, ExitCode, IO, IOApp, Timer}
import cats.syntax.flatMap._
import dev.alvo.shared.config.ConfigurationReader
import dev.alvo.todo.application.{HttpApplication, InMemoryStorageHttpApplication, MongodbStorageHttpApplication}
import dev.alvo.todo.config.TodoConfiguration
import fs2.Stream
import org.http4s.HttpApp
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import pureconfig.ConfigSource

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.global

object Main extends IOApp {

  type FStream[F[_]] = Stream[F, ExitCode]

  def run(args: List[String]): IO[ExitCode] =
    for {
      config <- ConfigurationReader[IO, TodoConfiguration].flatMap(_.loadConfiguration(ConfigSource.default))
      app <- createApplication[IO](config)
      server <- stream[IO](config, app).compile.drain.as(ExitCode.Success)
    } yield server

  private def stream[F[_]: ConcurrentEffect: Timer: ContextShift](
    conf: TodoConfiguration,
    app: HttpApp[F]
  ): FStream[F] =
    for {
      finalHttpApp <- Stream(Logger.httpApp(logHeaders = conf.log.logHeaders, logBody = conf.log.logBody)(app))
      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(conf.http.port, conf.http.host)
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode

  private def createApplication[F[_]: ConcurrentEffect: ContextShift: Timer](config: TodoConfiguration): F[HttpApp[F]] =
    config.application.mode match {
      case "in-memory" => createEntrypoint(config, InMemoryStorageHttpApplication[F])
      case "mongo" =>
        createEntrypoint(
          config,
          MongodbStorageHttpApplication[F](ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(4)))
        )
      case _ => createEntrypoint(config, InMemoryStorageHttpApplication[F])
    }

  private def createEntrypoint[F[_]: ConcurrentEffect: ContextShift: Timer](
    config: TodoConfiguration,
    applicationEffect: F[HttpApplication[F]]
  ): F[HttpApp[F]] = applicationEffect.flatMap(_.createEntrypoint(config))
}
