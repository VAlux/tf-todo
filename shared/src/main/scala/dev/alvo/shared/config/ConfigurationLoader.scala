package dev.alvo.shared.config

import pureconfig.error.ConfigReaderFailures
import pureconfig.{ConfigReader, ConfigSource}

sealed trait ConfigurationLoader[S, R, E] {
  def loadConfig(configSource: S): Either[E, R]
  def default: R
  def formatError(error: E): String
}

object ConfigurationLoader {
  trait PureconfigConfigurationLoader[C] extends ConfigurationLoader[ConfigSource, C, ConfigReaderFailures] {
    def loadConfig(configSource: ConfigSource): ConfigReader.Result[C]
    def default: C

    override def formatError(error: ConfigReaderFailures): String = error.prettyPrint()
  }
}
