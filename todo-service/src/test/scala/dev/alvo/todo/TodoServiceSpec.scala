package dev.alvo.todo

import cats.effect.concurrent.Ref
import cats.effect.{Concurrent, ContextShift, IO, Sync, Timer}
import cats.implicits._
import dev.alvo.shared.util.UUIDGenerator
import dev.alvo.todo.endpoints.application.TodoEndpoints
import dev.alvo.todo.model.User
import dev.alvo.todo.model.authentication.JwtAuthenticationServiceDescriptor
import dev.alvo.todo.model.response.UnauthorizedResponse
import dev.alvo.todo.routes.TodoRoutes
import dev.alvo.todo.service.TodoService
import dev.alvo.todo.service.authentication.JwtAuthenticationService
import dev.alvo.todo.repository.InMemoryTodoRepository
import dev.alvo.todo.repository.model.{Existing, Task}
import org.http4s._
import org.http4s.implicits._
import org.specs2.matcher.MatchResult

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

  private def mockAuthenticationService[F[_]](implicit F: Sync[F]): JwtAuthenticationService[F] =
    (descriptor: JwtAuthenticationServiceDescriptor) =>
      F.delay {
        if (descriptor.token == "secret") Right(User("email@email.com", "admin"))
        else Left(UnauthorizedResponse())
    }

  private[this] def createService[F[_]: Concurrent: ContextShift: Timer](request: Request[F]): F[Response[F]] =
    for {
      generator <- UUIDGenerator[F]
      engine <- Ref.of(Map.empty[String, Existing])
      storage <- InMemoryTodoRepository(engine, generator)
      service <- TodoService.create(storage)
      endpoints = new TodoEndpoints[F](service, mockAuthenticationService)
      todo <- TodoRoutes.create(endpoints).flatMap(_.routes.orNotFound(request))
    } yield todo

  private[this] val retAllTodo: Response[IO] =
    createService[IO](
      Request(Method.GET, uri"api/v1/todo", headers = Headers.of(Header("Authorization", "Bearer secret")))
    ).unsafeRunSync()

  private[this] def uriReturns200(): MatchResult[Status] =
    retAllTodo.status must beEqualTo(Status.Ok)

  private[this] def retrieveEmptyTodoList(): MatchResult[String] =
    retAllTodo.as[String].unsafeRunSync() must beEqualTo("[]")
}
