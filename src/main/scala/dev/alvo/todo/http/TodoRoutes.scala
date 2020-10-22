package dev.alvo.todo.http

import cats.FlatMap
import cats.effect.Sync
import cats.implicits._
import dev.alvo.todo.http.model.request.CreateTaskRequest
import dev.alvo.todo.http.model.request.CreateTaskRequest.createTaskRequestEntityDecoder
import dev.alvo.todo.http.model.response.RetrieveTaskResponse
import dev.alvo.todo.http.model.response.RetrieveTaskResponse.retrieveTaskResponseEntityEncoder
import dev.alvo.todo.storage.TodoStorage
import dev.alvo.todo.storage.model.Task
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, Response}
import scala.util.chaining._

object TodoRoutes {

  def todoServiceRoutes[F[_]: Sync](todo: TodoStorage[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    def toRetrievedTaskResponse[G[_]: FlatMap](tasks: G[Task.Existing]): G[F[Response[F]]] =
      tasks.map(task => Ok(RetrieveTaskResponse(task.id, task.action)))

    HttpRoutes.of[F] {
      case request @ POST -> Root / "todo" =>
        for {
          task <- request.as[CreateTaskRequest]
          added <- todo.add(Task.New(task.action))
          response <- toRetrievedTaskResponse(added).getOrElse(NotFound())
        } yield response
      case GET -> Root / "todo" =>
        for {
          tasks <- todo.getAll
          response <- tasks.map(task => RetrieveTaskResponse(task.id, task.action)).pipe(Ok(_))
        } yield response
      case GET -> Root / "todo" / todoId =>
        for {
          existing <- todo.get(todoId)
          response <- toRetrievedTaskResponse(existing).getOrElse(NotFound())
        } yield response
    }

  }
}
