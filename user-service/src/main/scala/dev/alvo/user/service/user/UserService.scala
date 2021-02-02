package dev.alvo.user.service.user

import cats.effect.{Async, ContextShift}
import dev.alvo.mongodb.MongoDb
import dev.alvo.mongodb.MongoDb.FutureOps
import dev.alvo.user.model.User
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, Macros}

import scala.concurrent.ExecutionContext

trait UserService[F[_]] {
  def findUserByEmail(email: String): F[Option[User]]

  def registerUser(user: User): F[Option[User]]

  def disableUser(email: String): F[Option[User]]
}

object UserService {
  import cats.syntax.apply._
  import cats.syntax.flatMap._
  import cats.syntax.functor._

  def apply[F[_]: ContextShift](mongo: MongoDb[F])(implicit F: Async[F], ec: ExecutionContext): F[UserService[F]] =
    mongo.getDatabase("user").map(_.collection[BSONCollection]("profiles")).map { profiles =>
      new UserService[F] {

        implicit val userWriter: BSONDocumentWriter[User] = Macros.writer
        implicit val userReader: BSONDocumentReader[User] = Macros.reader

        def emailFilter(email: String): BSONDocument = BSONDocument("email" -> email)

        override def findUserByEmail(email: String): F[Option[User]] =
          profiles.find(emailFilter(email)).one[User].asAsyncEffect

        override def registerUser(user: User): F[Option[User]] =
          for {
            exists <- profiles.count(Some(emailFilter(user.email))).asAsyncEffect.map(_ > 0)
            inserted <- if (!exists) profiles.insert.one(user).asAsyncEffect *> findUserByEmail(user.email)
            else F.pure(None)
          } yield inserted

        override def disableUser(email: String): F[Option[User]] =
          profiles
            .findAndUpdate(
              emailFilter(email),
              BSONDocument(f"$$set" -> BSONDocument("disabled" -> true)),
              fetchNewObject = true
            )
            .map(_.result)
            .asAsyncEffect
      }
    }
}
