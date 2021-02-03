package dev.alvo.mongodb

import cats.effect.{Async, ContextShift}
import dev.alvo.mongodb.config.MongoBaseConfig
import reactivemongo.api.{AsyncDriver, DB, MongoConnection}

import scala.concurrent.{ExecutionContext, Future}

trait MongoDb[F[_]] {
  def getDatabase(databaseName: String): F[DB]
}

object MongoDb {
  private val driver = AsyncDriver()

  def apply[F[_]: ContextShift](config: MongoBaseConfig)(implicit F: Async[F], ec: ExecutionContext): F[MongoDb[F]] =
    MongoConnection
      .fromString(config.host)
      .flatMap(uri => driver.connect(uri))
      .map(connection => createMongoDsl(connection))
      .asAsyncEffect

  private def createMongoDsl[F[_]: ContextShift](
    connection: MongoConnection
  )(implicit F: Async[F], ec: ExecutionContext): MongoDb[F] =
    (databaseName: String) => connection.database(databaseName).asAsyncEffect

  implicit class FutureOps[A](private val future: Future[A]) extends AnyVal {
    def asAsyncEffect[F[_]: ContextShift](implicit F: Async[F]): F[A] =
      Async.fromFuture(F.delay(future))
  }
}
