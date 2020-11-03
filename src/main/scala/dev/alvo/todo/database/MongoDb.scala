package dev.alvo.todo.database

import cats.effect.Sync
import cats.syntax.all._
import dev.alvo.todo.config.Configuration
import dev.alvo.todo.storage.model.Task
import org.bson.codecs.configuration.CodecRegistries.fromProviders
import org.bson.codecs.configuration.{CodecRegistries, CodecRegistry}
import org.mongodb.scala.bson.codecs.Macros
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}

import scala.reflect.ClassTag

trait MongoDb[F[_]] {

  def getDatabase(database: String): F[MongoDatabase]

  def getCollection[A: ClassTag](database: MongoDatabase, collection: String): F[MongoCollection[A]]
}

object MongoDb {

  def dsl[F[_]](config: Configuration)(implicit F: Sync[F]): F[MongoDb[F]] =
    F.delay(MongoClient(config.mongo.host)).map { client =>
      val todoCodecProvider = Macros.createCodecProvider[Task]

      val codecRegistry: CodecRegistry =
        CodecRegistries.fromRegistries(fromProviders(todoCodecProvider), MongoClient.DEFAULT_CODEC_REGISTRY)

      new MongoDb[F] {
        override def getDatabase(database: String): F[MongoDatabase] =
          F.delay(client.getDatabase(database).withCodecRegistry(codecRegistry))

        override def getCollection[A: ClassTag](database: MongoDatabase, collection: String): F[MongoCollection[A]] =
          F.delay(database.getCollection(collection))
      }
    }
}
