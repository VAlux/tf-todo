package dev.alvo.todo.config

case class Configuration(http: HttpBaseConfig, log: LoggingBaseConfig, mongo: MongoBaseConfig)

final case class HttpBaseConfig(host: String, port: Int)
final case class LoggingBaseConfig(logBody: Boolean, logHeaders: Boolean)
final case class MongoBaseConfig(host: String)

object Configuration {
  val default: Configuration =
    Configuration(
      HttpBaseConfig("0.0.0.0", 8080),
      LoggingBaseConfig(logBody = true, logHeaders = true),
      MongoBaseConfig("mongodb://localhost")
    )
}
