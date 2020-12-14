package dev.alvo.todo

import cats.effect.{ConcurrentEffect, ContextShift, ExitCode, IO, IOApp, Timer}
import cats.syntax.flatMap._
import dev.alvo.todo.application.{HttpApplication, InMemoryStorageHttpApplication, MongodbStorageHttpApplication}
import dev.alvo.todo.config.{ApplicationConfig, Configuration, ConfigurationReader}
import fs2.Stream
import org.http4s.HttpApp
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import pureconfig.error.ConfigReaderFailures

import scala.concurrent.ExecutionContext.global

object Main extends IOApp {

  private def processConfigurationLoadingError(error: ConfigReaderFailures): Configuration = {
    Console.err.println("Error reading configuration:")
    Console.err.println(error.prettyPrint())
    Console.err.println("Falling back to the default one...")
    Configuration.default
  }

  def stream[F[_]: ConcurrentEffect: Timer: ContextShift](conf: Configuration, app: HttpApp[F]): Stream[F, ExitCode] =
    for {
      finalHttpApp <- Stream(Logger.httpApp(logHeaders = conf.log.logHeaders, logBody = conf.log.logBody)(app))
      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(conf.http.port, conf.http.host)
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode

  def run(args: List[String]): IO[ExitCode] =
    for {
      config <- ConfigurationReader
        .dsl[IO]
        .flatMap(_.loadConfiguration)
        .map(_.fold(processConfigurationLoadingError, identity))
      app <- createApplication[IO](config)
      server <- stream[IO](config, app).compile.drain.as(ExitCode.Success)
    } yield server

  def createApplication[F[_]: ConcurrentEffect: ContextShift: Timer](config: Configuration): F[HttpApp[F]] =
    config.application match {
      case ApplicationConfig("in-memory") => createEntrypoint(config, InMemoryStorageHttpApplication[F])
      case ApplicationConfig("mongo") => createEntrypoint(config, MongodbStorageHttpApplication[F])
      case _ => InMemoryStorageHttpApplication[F].flatMap(_.createEntrypoint(config))
    }

  private def createEntrypoint[F[_]: ConcurrentEffect: ContextShift: Timer](
    config: Configuration,
    applicationEffect: F[HttpApplication[F]]
  ): F[HttpApp[F]] = applicationEffect.flatMap(_.createEntrypoint(config))
}
