package dev.alvo.todo

import cats.effect.concurrent.Ref
import cats.effect.{ConcurrentEffect, ContextShift, ExitCode, IO, IOApp, Timer}
import cats.implicits._
import dev.alvo.todo.config.{ConfigurationBasis, ConfigurationReader}
import dev.alvo.todo.http.TodoRoutes
import dev.alvo.todo.storage.InMemoryTodoStorage
import dev.alvo.todo.storage.model.Task
import fs2.Stream
import org.http4s.implicits._
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
    Conf: ConfigurationReader[F]
  )(implicit T: Timer[F], C: ContextShift[F]): Stream[F, Nothing] = {
    for {
      conf <- Stream.eval(Conf.loadConfiguration).map(_.fold(processConfigurationLoadingError, identity))
      todoService <- Stream.eval(Ref.of(Map.empty[String, Task.Existing]).map(InMemoryTodoStorage.dsl(_)))
      todoRoutes = new TodoRoutes[F](todoService).todoServiceRoutes
      finalHttpApp = Logger.httpApp(logHeaders = conf.log.logHeaders, logBody = conf.log.logBody)(todoRoutes.orNotFound)
      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(conf.http.port, conf.http.host)
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain

  def run(args: List[String]): IO[ExitCode] =
    for {
      config <- ConfigurationReader[IO]
      server <- stream[IO](config).compile.drain.as(ExitCode.Success)
    } yield server
}
