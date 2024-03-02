import org.typelevel.scalacoptions.{ScalaVersion, ScalacOption, ScalacOptions}
import deps.*
import sbt.internal.*
import StateSyntax.*
import sbt.Project.projectToRef

import scala.Ordering.Implicits.*
import sbtprojectmatrix.ProjectMatrixKeys

import scala.sys.process.Process

ThisBuild / organization     := "ru.tinkoff"
ThisBuild / organizationName := "Tinkoff"
ThisBuild / scalaVersion     := scalac.v3

lazy val scala2Versions     = List(scalac.v2_13)
lazy val scala3Versions     = List(scalac.v3)
lazy val scala2And3Versions = scala3Versions ++ scala2Versions

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

val s201name = "s2-01-scala3-overview"
lazy val `s2-01-scala3-overview` = (projectMatrix in file(s"modules/$s201name"))
  .settings(commonSettings)
  .settings(
    name := s201name,
    libraryDependencies ++= Seq(
      circe.core,
      munit % Test
    )
  )
  .jvmPlatform(scala2And3Versions)

val s202name = "s2-02-metaprogramming-1"
lazy val `s2-02-metaprogramming-1` = (project in file(s"modules/$s202name"))
  .settings(commonSettings)
  .settings(
    name := s202name,
    libraryDependencies ++= Seq(
      circe.core,
      munit % Test
    )
  )

val s203name = "s2-03-metaprogramming-2"
lazy val `s2-03-metaprogramming-2` = (project in file(s"modules/$s203name"))
  .settings(commonSettings)
  .settings(
    name := s203name,
    libraryDependencies ++= Seq(
      circe.core,
      circe.parse,
      munit % Test
    )
  )

lazy val allModules =
  Seq(
    `s2-01-scala3-overview`, // cross build projects
  ).flatMap(_.projectRefs) ++ Seq(
    `s2-02-metaprogramming-1`, // scala 3 only projects
    `s2-03-metaprogramming-2`
  ).map(projectToRef)

lazy val `root` = (project in file("."))
  .settings(
    name := "root",
    publish / skip := true
  )
  .aggregate(allModules: _*)

// map task -> module to compile
lazy val moduleKeys: Map[String, String] = {
  List(
    s202name,
  ).map(x => x.take(5) -> x).toMap + (
    "s2-01" -> (s201name + "3") // 3 is for scala3 module in sbt matrix, only for cross-build modules
  )
}

commands += Command.command("hw") { state =>
  val branch = Process("git rev-parse --abbrev-ref HEAD").lineStream.headOption

  val pattern = """solution-(s\d-\d\d).*""".r
  branch.flatMap {
    case pattern(x) =>
      val key = moduleKeys.get(x)
      if (key.isEmpty)
        sLog.value.warn(s"WARN: Current branch starts with 'solution-' prefix, but $x doesn't correspond to any module")
      key
    case _ => None
  }.fold(
    runCommand("test", state)
  )(m => runCommand(s"$m / test", state))
}
