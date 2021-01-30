val Http4sVersion = "0.21.5"
val CirceVersion = "0.13.0"
val Specs2Version = "4.10.0"
val LogbackVersion = "1.2.3"
val TypesafeConfigVersion = "1.4.0"
val PureconfigVersion = "0.14.0"
val MongoDriverVersion = "2.9.0"
val TapirVersion = "0.17.0-M9"
val ScalaBcryptVersion = "4.1"

lazy val commonSettings = Seq(
  organization := "dev.alvo",
  scalaVersion := "2.13.2",
  scalafmtOnCompile := true,
)

lazy val root = (project in file("."))
  .enablePlugins(NativeImagePlugin)
  .settings(
    commonSettings,
    name := "core",
    version := "0.0.1-SNAPSHOT",
    libraryDependencies ++= Seq(
      "org.http4s"                  %% "http4s-blaze-server"      % Http4sVersion,
      "org.http4s"                  %% "http4s-blaze-client"      % Http4sVersion,
      "org.http4s"                  %% "http4s-circe"             % Http4sVersion,
      "org.http4s"                  %% "http4s-dsl"               % Http4sVersion,
      "io.circe"                    %% "circe-generic"            % CirceVersion,
      "io.circe"                    %% "circe-core"               % CirceVersion,
      "ch.qos.logback"              %  "logback-classic"          % LogbackVersion,
      "org.mongodb.scala"           %% "mongo-scala-driver"       % MongoDriverVersion,
      "com.github.pureconfig"       %% "pureconfig"               % PureconfigVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-core"               % TapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"      % TapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe"         % TapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"       % TapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % TapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-http4s"  % TapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-redoc-http4s"       % TapirVersion,
      "com.github.t3hnar"           %% "scala-bcrypt"             % ScalaBcryptVersion,
      "org.specs2"                  %% "specs2-core"              % Specs2Version % "test"),
    Compile / mainClass := Some("dev.alvo.todo.Main"),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.10.3"),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1")
  )

// Swagger-ui assembly settings
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", "maven", "org.webjars", "swagger-ui", "pom.properties") =>
    MergeStrategy.singleOrError
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Xfatal-warnings",
)
