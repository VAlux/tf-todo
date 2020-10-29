package dev.alvo.todo.config

case class ConfigurationBasis(http: HttpBaseConfig, log: LoggingBaseConfig)

final case class HttpBaseConfig(host: String, port: Int)
final case class LoggingBaseConfig(logBody: Boolean, logHeaders: Boolean)

object ConfigurationBasis {
  val default: ConfigurationBasis =
    ConfigurationBasis(HttpBaseConfig("0.0.0.0", 8080), LoggingBaseConfig(logBody = true, logHeaders = true))
}
