package dev.alvo.user.config

import dev.alvo.mongodb.config.MongoBaseConfig
import dev.alvo.shared.config.ConfigurationLoader
import pureconfig.ConfigReader.Result
import pureconfig.generic.ProductHint
import pureconfig.{CamelCase, ConfigFieldMapping, ConfigSource}
import pureconfig.generic.auto._

final case class HttpBaseConfig(host: String, port: Int)
final case class LoggingBaseConfig(logBody: Boolean, logHeaders: Boolean)

case class UserConfiguration(http: HttpBaseConfig, log: LoggingBaseConfig, mongo: MongoBaseConfig)

object UserConfiguration {

  //scalafix:off RemoveUnused
  implicit private[this] def hint[A]: ProductHint[A] = ProductHint[A](ConfigFieldMapping(CamelCase, CamelCase))

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
