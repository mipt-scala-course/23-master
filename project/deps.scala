import sbt.*

object deps {
  val kindProjector = ("org.typelevel" %% "kind-projector"     % "0.13.2").cross(CrossVersion.full)
  val bmFor         = ("com.olegpy"    %% "better-monadic-for" % "0.3.1")

  val munit = "org.scalameta" %% "munit" % "0.7.29" % Test

  object circe {
    val version = "0.14.6"
    val core    = "io.circe" %% "circe-core"    % version
    val parse   = "io.circe" %% "circe-parser"  % version
    val generic = "io.circe" %% "circe-generic" % version,
  }

  object cats {
    val core            = "org.typelevel" %% "cats-core"                     % "2.10.0"
    val effect          = "org.typelevel" %% "cats-effect"                   % "3.5.4"
    val effectScalatest = "org.typelevel" %% "cats-effect-testing-scalatest" % "1.5.0" % Test
  }

  object http4s {
    val blazeClient = "org.http4s" %% "http4s-blaze-client" % "0.23.15"
  }

  object redis {
    val version = "1.6.0"

    val catsEffect = "dev.profunktor" %% "redis4cats-effects"  % version
    val catsLogs   = "dev.profunktor" %% "redis4cats-log4cats" % version
  }

  object testcontainers {
    val version              = "0.41.2"
    val core                 = "com.dimafeng"   %% "testcontainers-scala-scalatest"  % version  % Test
    val redis                = "com.dimafeng"   %% "testcontainers-scala-redis"      % version  % Test
    val mockServer           = "com.dimafeng"   %% "testcontainers-scala-mockserver" % version  % Test
    val mockServerJavaClient = "org.mock-server" % "mockserver-client-java"          % "5.13.2" % Test
  }

  object scalamock {
    val core      = "eu.monniot" %% "scala3mock"           % "0.6.0" % Test
    val scalaTest = "eu.monniot" %% "scala3mock-scalatest" % "0.6.0" % Test
  }

  val scalaTest = "org.scalatest" %% "scalatest" % "3.2.18" % Test
}

object scalac {
  val v2_13 = "2.13.12"
  val v3    = "3.3.2"
}
