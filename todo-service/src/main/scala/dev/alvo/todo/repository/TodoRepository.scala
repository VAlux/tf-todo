package dev.alvo.todo.repository

import dev.alvo.todo.repository.model.{Existing, New, Task}

trait TodoRepository[F[_]] {
  def add(task: New): F[Option[Existing]]

  def get(id: String): F[Option[Existing]]

  def update(id: String, task: New): F[Option[Existing]]

  def getAll: F[List[Existing]]

  def remove(id: String): F[Unit]

  def clear(): F[Unit]
}
