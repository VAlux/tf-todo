val Http4sVersion = "0.21.5"
val CirceVersion = "0.13.0"
val Specs2Version = "4.10.0"
val LogbackVersion = "1.2.3"
val TypesafeConfigVersion = "1.4.0"
val PureconfigVersion = "0.14.0"
val MongoDriverVersion = "2.9.0"
val TapirVersion = "0.17.0-M9"
val ScalaBcryptVersion = "4.1"
val ReactiveMongoVersion = "1.0.2"
val CatsVersion = "2.2.0"

val webserviceDependencies = Seq(
  "io.circe" %% "circe-generic" % CirceVersion,
  "io.circe" %% "circe-core" % CirceVersion,
  "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
  "org.http4s" %% "http4s-circe" % Http4sVersion,
  "org.http4s" %% "http4s-dsl" % Http4sVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-core" % TapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % TapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % TapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % TapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % TapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-http4s" % TapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-redoc-http4s" % TapirVersion,
  "org.specs2" %% "specs2-core" % Specs2Version % "test"
)

inThisBuild(
  List(
    scalaVersion := "2.13.6",
    organization := "dev.alvo",
    scalafmtOnCompile := true,
    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3"),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
  )
)

lazy val root = (project in file("."))
  .aggregate(shared, mongo, todo, user)

lazy val shared = (project in file("shared"))
  .settings(
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "com.github.pureconfig" %% "pureconfig" % PureconfigVersion,
      "org.typelevel" %% "cats-core" % CatsVersion,
      "org.typelevel" %% "cats-effect" % CatsVersion,
    )
  )

lazy val mongo = (project in file("mongo-db"))
  .dependsOn(shared)
  .settings(
    libraryDependencies ++= Seq(
      "org.reactivemongo" %% "reactivemongo" % ReactiveMongoVersion,
    )
  )

lazy val todo = (project in file("todo-service"))
  .enablePlugins(NativeImagePlugin)
  .dependsOn(mongo, shared)
  .settings(
    name := "todo-service",
    version := "0.0.1-SNAPSHOT",
    libraryDependencies ++= webserviceDependencies,
    libraryDependencies ++= Seq(
      "com.github.t3hnar" %% "scala-bcrypt" % ScalaBcryptVersion
    ),
    Compile / mainClass := Some("dev.alvo.todo.Main")
  )

lazy val user = (project in file("user-service"))
  .enablePlugins(NativeImagePlugin)
  .dependsOn(mongo, shared)
  .settings(
    name := "user-service",
    version := "0.0.1-SNAPSHOT",
    libraryDependencies ++= webserviceDependencies
  )

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", "maven", "org.webjars", "swagger-ui", "pom.properties") =>
    MergeStrategy.singleOrError
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Wunused",
  "-Yrangepos"
)

addCommandAlias("cd", "project")
addCommandAlias("ls", "projects")
addCommandAlias("c", "compile")
addCommandAlias("r", "reload")
