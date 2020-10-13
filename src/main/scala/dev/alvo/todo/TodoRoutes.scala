package dev.alvo.todo

import cats.effect.Sync
import cats.implicits._
import dev.alvo.todo.TodoStorage.Task
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl

object TodoRoutes {

  def todoServiceRoutes[F[_] : Sync](todo: TodoStorage[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    HttpRoutes.of[F] {
      case request@POST -> Root / "todo" => for {
        task <- request.as[Task]
        _ <- todo.add(task)
        response <- Ok(todo.getAll)
      } yield response
      case GET -> Root / "todo" =>
        Ok(todo.getAll)
    }
  }
}