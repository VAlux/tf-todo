package dev.alvo.todo.storage

import dev.alvo.todo.storage.model.Task

trait TodoStorage[F[_]] {
  def add(task: Task.New): F[Option[Task.Existing]]

  def get(id: String): F[Option[Task.Existing]]

  def getAll: F[List[Task.Existing]]

  def remove(id: String): F[Unit]

  def clear(): F[Unit]
}
