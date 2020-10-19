package dev.alvo.todo

import cats.effect.concurrent.Ref
import cats.effect.{ConcurrentEffect, ContextShift, ExitCode, IO, IOApp, Timer}
import cats.implicits._
import dev.alvo.todo.http.TodoRoutes.todoServiceRoutes
import dev.alvo.todo.UUIDGenerator.dsl
import dev.alvo.todo.storage.InMemoryTodoStorage
import dev.alvo.todo.storage.model.Task
import fs2.Stream
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger

import scala.concurrent.ExecutionContext.global

object Main extends IOApp {
  def stream[F[_]: ConcurrentEffect: UUIDGenerator](implicit T: Timer[F], C: ContextShift[F]): Stream[F, Nothing] = {
    for {
      todoService <- Stream.eval(Ref.of(Map.empty[String, Task]).map(store => InMemoryTodoStorage.dsl(store)))
      todoRoutes = todoServiceRoutes(todoService)
      finalHttpApp = Logger.httpApp(logHeaders = true, logBody = true)(todoRoutes.orNotFound)
      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain

  def run(args: List[String]): IO[ExitCode] =
    stream[IO].compile.drain.as(ExitCode.Success)
}
