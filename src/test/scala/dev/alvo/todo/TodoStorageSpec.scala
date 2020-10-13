package dev.alvo.todo

import cats.effect.concurrent.Ref
import cats.effect.{IO, Sync}
import cats.implicits._
import dev.alvo.todo.TodoStorage.Task
import org.http4s._
import org.http4s.implicits._
import org.specs2.matcher.MatchResult

class TodoStorageSpec extends org.specs2.mutable.Specification {

  "Todo" >> {
    "return 200" >> {
      uriReturns200()
    }
    "retrieve empty todo list" >> {
      retrieveEmptyTodoList()
    }
  }

  private[this] def createService[F[_] : Sync](request: Request[F]): F[Response[F]] = for {
    storage <- Ref.of(Map.empty[String, Task])
    service = TodoStorage.impl(storage)
    todo <- TodoRoutes.todoServiceRoutes(service).orNotFound(request)
  } yield todo


  private[this] val retAllTodo: Response[IO] =
    createService[IO](Request(Method.GET, uri"/todo")).unsafeRunSync()

  private[this] def uriReturns200(): MatchResult[Status] =
    retAllTodo.status must beEqualTo(Status.Ok)

  private[this] def retrieveEmptyTodoList(): MatchResult[String] =
    retAllTodo.as[String].unsafeRunSync() must beEqualTo("{\"tasks\":[]}")
}