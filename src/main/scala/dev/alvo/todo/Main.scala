package dev.alvo.todo

import cats.effect.{ConcurrentEffect, ContextShift, ExitCode, IO, IOApp, Timer}
import dev.alvo.todo.config.{ConfigurationBasis, ConfigurationReader}
import dev.alvo.todo.http.App
import fs2.Stream
import org.http4s.HttpApp
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import pureconfig.error.ConfigReaderFailures

import scala.concurrent.ExecutionContext.global

object Main extends IOApp {

  private def processConfigurationLoadingError(error: ConfigReaderFailures): ConfigurationBasis = {
    Console.err.println("Error reading configuration:")
    Console.err.println(error.prettyPrint())
    Console.err.println("Falling back to the default one...")
    ConfigurationBasis.default
  }

  def stream[F[_]: ConcurrentEffect](
    conf: ConfigurationReader[F],
    app: HttpApp[F]
  )(implicit T: Timer[F], C: ContextShift[F]): Stream[F, Nothing] = {
    for {
      conf <- Stream.eval(conf.loadConfiguration).map(_.fold(processConfigurationLoadingError, identity))
      finalHttpApp = Logger.httpApp(logHeaders = conf.log.logHeaders, logBody = conf.log.logBody)(app)
      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(conf.http.port, conf.http.host)
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain

  def run(args: List[String]): IO[ExitCode] =
    for {
      app <- App.create[IO]
      config <- ConfigurationReader[IO]
      server <- stream[IO](config, app).compile.drain.as(ExitCode.Success)
    } yield server
}
