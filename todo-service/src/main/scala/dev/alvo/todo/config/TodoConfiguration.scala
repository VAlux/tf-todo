package dev.alvo.todo.config

import dev.alvo.mongodb.config.MongoBaseConfig
import dev.alvo.shared.config.ConfigurationLoader.PureconfigConfigurationLoader
import pureconfig.ConfigReader.Result
import pureconfig.generic.ProductHint
import pureconfig.{CamelCase, ConfigFieldMapping, ConfigSource}
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

  //scalafix:off RemoveUnused
  implicit private[this] def hint[A]: ProductHint[A] = ProductHint[A](ConfigFieldMapping(CamelCase, CamelCase))

  implicit object TodoConfigurationLoader extends PureconfigConfigurationLoader[TodoConfiguration] {
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
