import sbt.*

object deps {
  val kindProjector = ("org.typelevel"  %% "kind-projector"     % "0.13.2").cross(CrossVersion.full)
  val bmFor         = ("com.olegpy"     %% "better-monadic-for" % "0.3.1")

  val munit = "org.scalameta" %% "munit" % "0.7.29"

  object circe {
    val core = "io.circe" %% "circe-core" % "0.14.6"
  }
}

object scalac {
  val v2_13 = "2.13.12"
  val v3    = "3.3.1"
}
