package dev.alvo.todo.config

import dev.alvo.mongodb.config.MongoBaseConfig
import dev.alvo.shared.config.ConfigurationLoader
import pureconfig.ConfigReader.Result
import pureconfig.ConfigSource
import pureconfig.generic.auto._

final case class HttpBaseConfig(host: String, port: Int)
final case class LoggingBaseConfig(logBody: Boolean, logHeaders: Boolean)
final case class ApplicationConfig(mode: String)

case class TodoConfiguration(
  application: ApplicationConfig,
  http: HttpBaseConfig,
  log: LoggingBaseConfig,
  mongo: MongoBaseConfig
)

object TodoConfiguration {
  implicit object TodoConfigurationLoader extends ConfigurationLoader[TodoConfiguration] {
    override def loadConfig(configSource: ConfigSource): Result[TodoConfiguration] =
      configSource.load[TodoConfiguration]

    override def default: TodoConfiguration =
      TodoConfiguration(
        ApplicationConfig("in-memory"),
        HttpBaseConfig("0.0.0.0", 8080),
        LoggingBaseConfig(logBody = true, logHeaders = true),
        MongoBaseConfig("mongodb://localhost")
      )
  }
}
