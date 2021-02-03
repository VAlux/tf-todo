package dev.alvo.todo.service

import cats.effect.Sync
import cats.syntax.functor._
import dev.alvo.todo.model.request.CreateTaskRequest
import dev.alvo.todo.repository.TodoRepository
import dev.alvo.todo.repository.model.{Existing, New, Task}

trait TodoService[F[_]] {

  def createTask(request: CreateTaskRequest): F[Option[Existing]]

  def updateTask(todoId: String, request: CreateTaskRequest): F[Option[Existing]]

  def getTask(todoId: String): F[Option[Existing]]

  def getAllTasks: F[List[Existing]]

  def removeAllTasks(): F[String]

  def removeTask(todoId: String): F[String]
}

object TodoService {
  def apply[F[_]](todo: TodoRepository[F])(implicit F: Sync[F]): F[TodoService[F]] = F.delay {
    new TodoService[F] {
      override def createTask(request: CreateTaskRequest): F[Option[Existing]] =
        todo.add(New(request.action))

      override def getTask(todoId: String): F[Option[Existing]] =
        todo.get(todoId)

      override def getAllTasks: F[List[Existing]] =
        todo.getAll

      override def updateTask(todoId: String, request: CreateTaskRequest): F[Option[Existing]] =
        todo.update(todoId, New(request.action))

      override def removeTask(todoId: String): F[String] =
        todo.remove(todoId).map(_ => s"Todo with id $todoId is removed!")

      override def removeAllTasks(): F[String] =
        todo.clear().map(_ => s"All todos are removed!")
    }
  }
}
