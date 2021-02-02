package dev.alvo.todo.storage.model

sealed abstract class Task(action: String)

case class New(action: String) extends Task(action)

case class Existing(id: String, action: String) extends Task(action)
