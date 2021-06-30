package dev.alvo.user.repository

import cats.effect.Sync
import cats.effect.concurrent.Ref
import dev.alvo.user.model.User

object UserInMemoryRepository {
  import cats.syntax.apply._
  import cats.syntax.functor._
  import cats.syntax.flatMap._

  def apply[F[_]](storage: Ref[F, Map[String, User]])(implicit F: Sync[F]): F[UserRepository[F]] = F.delay {

    def updateUser(email: String, user: User): F[Option[User]] =
      storage.update(_.updated(email, user)) *> storage.get.map(_.get(email))

    new UserRepository[F] {
      override def findUserByEmail(email: String): F[Option[User]] =
        storage.get.map(_.get(email))

      override def registerUser(user: User): F[Option[User]] =
        updateUser(user.email, user)

      override def disableUser(email: String): F[Option[User]] =
        findUserByEmail(email).map(
          existing => existing.map(user => storage.update(_.updated(email, user.copy(disabled = true))))
        ) *> findUserByEmail(email)
    }
  }
}
