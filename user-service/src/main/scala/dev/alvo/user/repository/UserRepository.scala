package dev.alvo.user.repository

import dev.alvo.user.model.User

trait UserRepository[F[_]] {
  def findUserByEmail(email: String): F[Option[User]]

  def registerUser(user: User): F[Option[User]]

  def disableUser(email: String): F[Option[User]]
}
