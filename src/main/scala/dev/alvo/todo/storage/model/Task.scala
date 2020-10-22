package dev.alvo.todo.storage.model

sealed abstract class Task(action: String) extends Product with Serializable

object Task {

  final case class New(action: String) extends Task(action)

  final case class Existing(id: String, action: String) extends Task(action)

}
