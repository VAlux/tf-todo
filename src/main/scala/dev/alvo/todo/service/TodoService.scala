package dev.alvo.todo.service

import cats.effect.Sync
import cats.syntax.functor._
import dev.alvo.todo.model.request.CreateTaskRequest
import dev.alvo.todo.storage.TodoStorage
import dev.alvo.todo.storage.model.Task

trait TodoService[F[_]] {

  def createTask(request: CreateTaskRequest): F[Option[Task.Existing]]

  def updateTask(todoId: String, request: CreateTaskRequest): F[Option[Task.Existing]]

  def getTask(todoId: String): F[Option[Task.Existing]]

  def getAllTasks: F[List[Task.Existing]]

  def removeAllTasks(): F[String]

  def removeTask(todoId: String): F[String]
}

object TodoService {
  def create[F[_]](todo: TodoStorage[F])(implicit F: Sync[F]): F[TodoService[F]] = F.delay {
    new TodoService[F] {
      override def createTask(request: CreateTaskRequest): F[Option[Task.Existing]] =
        todo.add(Task.New(request.action))

      override def getTask(todoId: String): F[Option[Task.Existing]] =
        todo.get(todoId)

      override def getAllTasks: F[List[Task.Existing]] =
        todo.getAll

      override def updateTask(todoId: String, request: CreateTaskRequest): F[Option[Task.Existing]] =
        todo.update(todoId, Task.New(request.action))

      override def removeTask(todoId: String): F[String] =
        todo.remove(todoId).map(_ => s"Todo with id $todoId is removed!")

      override def removeAllTasks(): F[String] =
        todo.clear().map(_ => s"All todos are removed!")
    }
  }
}
