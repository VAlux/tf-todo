val Http4sVersion = "0.21.5"
val CirceVersion = "0.13.0"
val Specs2Version = "4.10.0"
val LogbackVersion = "1.2.3"
val TypesafeConfigVersion = "1.4.0"
val PureconfigVersion = "0.14.0"
val MongoDriverVersion = "2.9.0"

lazy val root = (project in file("."))
  .settings(
    organization := "dev.alvo",
    name := "todo",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.2",
    scalafmtOnCompile := true,
    libraryDependencies ++= Seq(
      "org.http4s"            %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"            %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s"            %% "http4s-circe"        % Http4sVersion,
      "org.http4s"            %% "http4s-dsl"          % Http4sVersion,
      "io.circe"              %% "circe-generic"       % CirceVersion,
      "io.circe"              %% "circe-core"          % CirceVersion,
      "org.specs2"            %% "specs2-core"         % Specs2Version % "test",
      "ch.qos.logback"        %  "logback-classic"     % LogbackVersion,
      "org.mongodb.scala"     %% "mongo-scala-driver"  % MongoDriverVersion,
      "com.github.pureconfig" %% "pureconfig"          % PureconfigVersion
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.10.3"),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1")
  )

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Xfatal-warnings",
)
