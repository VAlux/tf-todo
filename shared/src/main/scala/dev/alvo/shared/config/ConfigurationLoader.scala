package dev.alvo.shared.config

import pureconfig.{ConfigReader, ConfigSource}

trait ConfigurationLoader[C] {
  def loadConfig(configSource: ConfigSource): ConfigReader.Result[C]
  def default: C
}
