package dev.alvo.user.config

import dev.alvo.mongodb.config.MongoBaseConfig
import dev.alvo.shared.config.ConfigurationLoader
import pureconfig.ConfigReader.Result
import pureconfig.ConfigSource
import pureconfig.generic.auto._

final case class HttpBaseConfig(host: String, port: Int)
final case class LoggingBaseConfig(logBody: Boolean, logHeaders: Boolean)

case class UserConfiguration(http: HttpBaseConfig, log: LoggingBaseConfig, mongo: MongoBaseConfig)

object UserConfiguration {
  implicit object UserConfigurationLoader extends ConfigurationLoader[UserConfiguration] {
    override def loadConfig(configSource: ConfigSource): Result[UserConfiguration] =
      configSource.load[UserConfiguration]

    override def default: UserConfiguration =
      UserConfiguration(
        HttpBaseConfig("0.0.0.0", 8080),
        LoggingBaseConfig(logBody = true, logHeaders = true),
        MongoBaseConfig("mongodb://localhost")
      )
  }
}
