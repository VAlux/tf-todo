package dev.alvo.user.service

import cats.effect.Sync
import dev.alvo.user.model.User
import dev.alvo.user.repository.UserRepository

trait UserService[F[_]] {
  def findUserByEmail(email: String): F[Option[User]]

  def registerUser(user: User): F[Option[User]]

  def disableUser(email: String): F[Option[User]]
}

object UserService {
  def apply[F[_]](userRepository: UserRepository[F])(implicit F: Sync[F]): F[UserService[F]] = F.delay {
    new UserService[F] {
      override def findUserByEmail(email: String): F[Option[User]] =
        userRepository.findUserByEmail(email)

      override def registerUser(user: User): F[Option[User]] =
        userRepository.registerUser(user)

      override def disableUser(email: String): F[Option[User]] =
        userRepository.disableUser(email)
    }
  }
}
