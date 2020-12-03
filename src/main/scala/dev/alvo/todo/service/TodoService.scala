package dev.alvo.todo.service

import cats.effect.Sync
import cats.syntax.functor._
import dev.alvo.todo.http.model.request.CreateTaskRequest
import dev.alvo.todo.http.model.response.{ErrorInfoResponse, NotFoundResponse, RetrieveTaskResponse, UserErrorResponse}
import dev.alvo.todo.storage.TodoStorage
import dev.alvo.todo.storage.model.Task

trait TodoService[F[_]] {

  type RetrieveOrErrorResponse = Either[ErrorInfoResponse, RetrieveTaskResponse]

  def createTask(request: CreateTaskRequest): F[RetrieveOrErrorResponse]

  def updateTask(todoId: String, request: CreateTaskRequest): F[RetrieveOrErrorResponse]

  def getTask(todoId: String): F[RetrieveOrErrorResponse]

  def getAllTasks: F[Either[ErrorInfoResponse, List[RetrieveTaskResponse]]]

  def removeAllTasks(): F[Either[ErrorInfoResponse, String]]

  def removeTask(todoId: String): F[Either[ErrorInfoResponse, String]]
}

object TodoService {
  def create[F[_]](todo: TodoStorage[F])(implicit F: Sync[F]): F[TodoService[F]] = F.delay {
    new TodoService[F] {
      override def removeAllTasks(): F[Either[ErrorInfoResponse, String]] =
        todo.clear().map(_ => Right(s"All todos are removed!").withLeft[UserErrorResponse])

      override def removeTask(todoId: String): F[Either[ErrorInfoResponse, String]] =
        todo.remove(todoId).map(_ => Right(s"Todo with id $todoId is removed!").withLeft[UserErrorResponse])

      override def getTask(todoId: String): F[Either[ErrorInfoResponse, RetrieveTaskResponse]] =
        for {
          existing <- todo.get(todoId)
          response = existing.fold[RetrieveOrErrorResponse](Left(NotFoundResponse()))(
            task => Right(RetrieveTaskResponse(task.id, task.action))
          )
        } yield response

      override def getAllTasks: F[Either[ErrorInfoResponse, List[RetrieveTaskResponse]]] =
        for {
          tasks <- todo.getAll
          response = tasks.map(task => RetrieveTaskResponse(task.id, task.action))
        } yield Right(response).withLeft[ErrorInfoResponse]

      override def updateTask(todoId: String, request: CreateTaskRequest): F[RetrieveOrErrorResponse] =
        for {
          updated <- todo.update(todoId, Task.New(request.action))
          response = updated.fold[RetrieveOrErrorResponse](Left(NotFoundResponse()))(
            task => Right(RetrieveTaskResponse(task.id, task.action))
          )
        } yield response

      override def createTask(request: CreateTaskRequest): F[RetrieveOrErrorResponse] =
        for {
          added <- todo.add(Task.New(request.action))
          response = added.fold[RetrieveOrErrorResponse](Left(NotFoundResponse()))(
            t => Right(RetrieveTaskResponse(t.id, t.action))
          )
        } yield response
    }
  }
}
