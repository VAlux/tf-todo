package dev.alvo.todo.storage

import dev.alvo.todo.storage.model.{Task, TasksModel}

trait TodoStorage[F[_]] {
  def add(task: Task): F[Unit]

  def get(id: String): F[Option[Task]]

  def getAll: F[TasksModel]

  def remove(id: String): F[Unit]

  def clear(): F[Unit]
}
