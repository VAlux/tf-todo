package dev.alvo.todo

import cats.effect.concurrent.Ref
import cats.effect.{Concurrent, ContextShift, IO, Timer}
import cats.implicits._
import dev.alvo.todo.endpoints.TodoEndpoints
import dev.alvo.todo.routes.TodoRoutes
import dev.alvo.todo.service.TodoService
import dev.alvo.todo.storage.InMemoryTodoStorage
import dev.alvo.todo.storage.model.Task
import org.http4s._
import org.http4s.implicits._
import org.specs2.matcher.MatchResult
import utils.UUIDGenerator

import scala.concurrent.ExecutionContext

class TodoServiceSpec extends org.specs2.mutable.Specification {

  implicit private def contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  implicit private def timer: Timer[IO] = IO.timer(ExecutionContext.global)

  "Todo" >> {
    "return 200" >> {
      uriReturns200()
    }
    "retrieve empty todo list" >> {
      retrieveEmptyTodoList()
    }
  }

  private[this] def createService[F[_]: Concurrent: ContextShift: Timer](request: Request[F]): F[Response[F]] =
    for {
      generator <- UUIDGenerator[F]
      engine <- Ref.of(Map.empty[String, Task.Existing])
      storage <- InMemoryTodoStorage(engine, generator)
      service <- TodoService.create(storage)
      endpoints = new TodoEndpoints[F](service)
      todo <- TodoRoutes.create(endpoints).flatMap(_.routes.orNotFound(request))
    } yield todo

  private[this] val retAllTodo: Response[IO] =
    createService[IO](Request(Method.GET, uri"api/v1.0/todo")).unsafeRunSync()

  private[this] def uriReturns200(): MatchResult[Status] =
    retAllTodo.status must beEqualTo(Status.Ok)

  private[this] def retrieveEmptyTodoList(): MatchResult[String] =
    retAllTodo.as[String].unsafeRunSync() must beEqualTo("[]")
}
