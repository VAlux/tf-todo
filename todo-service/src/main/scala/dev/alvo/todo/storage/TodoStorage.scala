package dev.alvo.todo.storage

import dev.alvo.todo.storage.model.{Existing, New, Task}

trait TodoStorage[F[_]] {
  def add(task: New): F[Option[Existing]]

  def get(id: String): F[Option[Existing]]

  def update(id: String, task: New): F[Option[Existing]]

  def getAll: F[List[Existing]]

  def remove(id: String): F[Unit]

  def clear(): F[Unit]
}
