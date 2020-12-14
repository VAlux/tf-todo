package dev.alvo.todo.config

case class Configuration(
  application: ApplicationConfig,
  http: HttpBaseConfig,
  log: LoggingBaseConfig,
  mongo: MongoBaseConfig
)

final case class HttpBaseConfig(host: String, port: Int)
final case class LoggingBaseConfig(logBody: Boolean, logHeaders: Boolean)
final case class MongoBaseConfig(host: String)
final case class ApplicationConfig(mode: String)

object Configuration {
  val default: Configuration =
    Configuration(
      ApplicationConfig("in-memory"),
      HttpBaseConfig("0.0.0.0", 8080),
      LoggingBaseConfig(logBody = true, logHeaders = true),
      MongoBaseConfig("mongodb://localhost")
    )
}
