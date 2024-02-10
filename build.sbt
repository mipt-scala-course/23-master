import org.typelevel.scalacoptions.{ScalaVersion, ScalacOption, ScalacOptions}
import deps.*
import sbt.internal.*
import StateSyntax.*
import scala.Ordering.Implicits.*
import sbtprojectmatrix.ProjectMatrixKeys
import scala.sys.process.Process

ThisBuild / organization     := "ru.tinkoff.sme"
ThisBuild / organizationName := "Tinkoff"
ThisBuild / versionScheme    := Some("early-semver")

lazy val scala2Versions     = List(scalac.v2_13)
lazy val scala2And3Versions = scalac.v3 :: scala2Versions

lazy val commonSettings = Seq(
  tpolecatExcludeOptions ++= Set(ScalacOptions.privateKindProjector),
  tpolecatScalacOptions ++= Set(
    ScalacOption("-Ykind-projector:underscores", _.isAtLeast(ScalaVersion.V3_0_0)),
    ScalacOption("-P:kind-projector:underscore-placeholders", _ < ScalaVersion.V3_0_0),
    ScalacOptions.source3,
    ScalacOption("-Xmigration", _ < ScalaVersion.V3_0_0),
    ScalacOptions.warnOption("macros:after", v => v.isAtLeast(ScalaVersion.V2_13_0) && v < ScalaVersion.V3_0_0),
    ScalacOptions.privateOption("macro-annotations", v => v.isAtLeast(ScalaVersion.V2_13_0) && v < ScalaVersion.V3_0_0),
    ScalacOptions.privatePartialUnification
  ),
  tpolecatExcludeOptions += ScalacOptions.privateWarnUnusedNoWarn,
  tpolecatExcludeOptions ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, _)) => Set(ScalacOptions.warnUnusedLocals)
      case _            => Set()
    }
  },
  Test / tpolecatExcludeOptions ++= Set(
    ScalacOptions.warnUnusedLocals,
    ScalacOptions.fatalWarnings,
    ScalacOptions.privateWarnUnusedLocals
  ),
  libraryDependencies ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 13)) =>
        Seq(
          compilerPlugin(deps.kindProjector),
          compilerPlugin(deps.bmFor)
        )
      case _ => Seq()
    }
  },
  publish / skip := true
)

lazy val `s2-01-scala3-overview` = (projectMatrix in file("modules/s2-01-scala3-overview"))
  .settings(commonSettings)
  .settings(
    name := "s2-01-scala3-overview",
    libraryDependencies ++= Seq(
      circe.core,
      munit % Test
    )
  )
  .jvmPlatform(scala2And3Versions)

lazy val allModules =
  Seq(
    `s2-01-scala3-overview`
  )

lazy val `root` = (project in file("."))
  .settings(
    name := "root",
    publish / skip := true
  )
  .aggregate(allModules.flatMap(_.projectRefs): _*)

commands += Command.command("hw") { state =>
  val branch = Process("git rev-parse --abbrev-ref HEAD").lineStream.head

  val pattern = """solution-(s\d-\d\d).*""".r
  branch match {
    case pattern("s2-01") =>
      runCommand("s2-01-scala3-overview3 / test", state)
    case pattern(x) =>
      sLog.value.warn(s"WARN: Current branch starts with 'solution-' prefix, but $x doesn't correspond to any module")
      runCommand("test", state)
    case _ =>
      runCommand("test", state)
  }
}
