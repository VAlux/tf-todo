package dev.alvo.todo.database

import cats.effect.Sync
import cats.syntax.all._
import dev.alvo.todo.config.Configuration
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}

import scala.reflect.ClassTag

trait MongoDb[F[_]] {

  def getDatabase(database: String): F[MongoDatabase]

  def getCollection[A: ClassTag](database: MongoDatabase, collection: String): F[MongoCollection[A]]
}

object MongoDb {

  def dsl[F[_]](config: Configuration)(implicit F: Sync[F]): F[MongoDb[F]] =
    F.delay(MongoClient(config.mongo.host)).map { client =>
      new MongoDb[F] {
        override def getDatabase(database: String): F[MongoDatabase] = F.delay(client.getDatabase(database))

        override def getCollection[A: ClassTag](database: MongoDatabase, collection: String): F[MongoCollection[A]] =
          F.delay(database.getCollection(collection))
      }
    }
}
