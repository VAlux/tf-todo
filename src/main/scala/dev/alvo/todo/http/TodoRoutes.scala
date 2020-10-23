package dev.alvo.todo.http

import cats.effect.Sync
import cats.implicits._
import dev.alvo.todo.http.model.request.CreateTaskRequest
import dev.alvo.todo.http.model.request.CreateTaskRequest.createTaskRequestEntityDecoder
import dev.alvo.todo.http.model.response.RetrieveTaskResponse
import dev.alvo.todo.http.model.response.RetrieveTaskResponse.retrieveTaskResponseEntityEncoder
import dev.alvo.todo.storage.TodoStorage
import dev.alvo.todo.storage.model.Task
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

import scala.util.chaining._

object TodoRoutes {

  def todoServiceRoutes[F[_]: Sync](todo: TodoStorage[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    HttpRoutes.of[F] {
      case request @ POST -> Root / "todo" =>
        for {
          task <- request.as[CreateTaskRequest]
          added <- todo.add(Task.New(task.action))
          response <- added.map(task => Ok(RetrieveTaskResponse(task.id, task.action))).getOrElse(NotFound())
        } yield response
      case request @ PATCH -> Root / "todo" / todoId =>
        for {
          request <- request.as[CreateTaskRequest]
          updated <- todo.update(todoId, Task.New(request.action))
          response <- updated.map(task => Ok(RetrieveTaskResponse(task.id, task.action))).getOrElse(NotFound())
        } yield response
      case GET -> Root / "todo" =>
        for {
          tasks <- todo.getAll
          response <- tasks.map(task => RetrieveTaskResponse(task.id, task.action)).pipe(Ok(_))
        } yield response
      case GET -> Root / "todo" / todoId =>
        for {
          existing <- todo.get(todoId)
          response <- existing.map(task => Ok(RetrieveTaskResponse(task.id, task.action))).getOrElse(NotFound())
        } yield response
      case DELETE -> Root / "todo" / todoId =>
        todo.remove(todoId) >> Ok(s"Todo with id $todoId is removed!")
    }
  }
}
