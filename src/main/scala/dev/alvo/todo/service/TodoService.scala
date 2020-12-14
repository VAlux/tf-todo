package dev.alvo.todo.service

import cats.effect.Sync
import cats.syntax.functor._
import dev.alvo.todo.model.request.CreateTaskRequest
import dev.alvo.todo.model.response.{ErrorResponse, NotFoundResponse, RetrieveTaskResponse, UserErrorResponse}
import dev.alvo.todo.storage.TodoStorage
import dev.alvo.todo.storage.model.Task

trait TodoService[F[_]] {

  type RetrieveOrErrorResponse = Either[ErrorResponse, RetrieveTaskResponse]
  type RetrieveListOrErrorResponse = Either[ErrorResponse, List[RetrieveTaskResponse]]

  def createTask(request: CreateTaskRequest): F[RetrieveOrErrorResponse]

  def updateTask(todoId: String, request: CreateTaskRequest): F[RetrieveOrErrorResponse]

  def getTask(todoId: String): F[RetrieveOrErrorResponse]

  def getAllTasks: F[Either[ErrorResponse, List[RetrieveTaskResponse]]]

  def removeAllTasks(): F[Either[ErrorResponse, String]]

  def removeTask(todoId: String): F[Either[ErrorResponse, String]]
}

object TodoService {
  def create[F[_]](todo: TodoStorage[F])(implicit F: Sync[F]): F[TodoService[F]] = F.delay {
    new TodoService[F] {

      override def createTask(request: CreateTaskRequest): F[RetrieveOrErrorResponse] =
        for {
          added <- todo.add(Task.New(request.action))
          response = added.fold[RetrieveOrErrorResponse](Left(NotFoundResponse()))(
            t => Right(RetrieveTaskResponse(t.id, t.action))
          )
        } yield response

      override def getTask(todoId: String): F[RetrieveOrErrorResponse] =
        for {
          existing <- todo.get(todoId)
          response = existing.fold[RetrieveOrErrorResponse](Left(NotFoundResponse()))(
            task => Right(RetrieveTaskResponse(task.id, task.action))
          )
        } yield response

      override def getAllTasks: F[RetrieveListOrErrorResponse] =
        for {
          tasks <- todo.getAll
          response = tasks.map(task => RetrieveTaskResponse(task.id, task.action))
        } yield Right[ErrorResponse, List[RetrieveTaskResponse]](response)

      override def updateTask(todoId: String, request: CreateTaskRequest): F[RetrieveOrErrorResponse] =
        for {
          updated <- todo.update(todoId, Task.New(request.action))
          response = updated.fold[RetrieveOrErrorResponse](Left(NotFoundResponse()))(
            task => Right(RetrieveTaskResponse(task.id, task.action))
          )
        } yield response

      override def removeTask(todoId: String): F[Either[ErrorResponse, String]] =
        todo.remove(todoId).map(_ => Right[UserErrorResponse, String](s"Todo with id $todoId is removed!"))

      override def removeAllTasks(): F[Either[ErrorResponse, String]] =
        todo.clear().map(_ => Right[UserErrorResponse, String](s"All todos are removed!"))
    }
  }
}
