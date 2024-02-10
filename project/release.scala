import sbt.Keys._
import sbt._
import sbt.librarymanagement.MavenRepository

object release {
  val publishSettings: Seq[Def.Setting[_]] = Seq(
    publish / skip                         := false,
    publishMavenStyle                      := true,
    Test / packageBin / publishArtifact    := false,
    Test / packageSrc / publishArtifact    := false,
    Compile / packageDoc / publishArtifact := false,
    pomIncludeRepository                   := { _ => false },
    version                                := versionFromEnv.getOrElse("local"),
    publishTo := Some(
      if (isSnapshot) repos.IbulSnapshot else repos.IbulRelease,
    ),
    credentials += Credentials(
      "Sonatype Nexus Repository Manager",
      "nexus.tcsbank.ru",
      System.getenv("IBUL_NEXUS_USER"),
      System.getenv("IBUL_NEXUS_PASSWORD"),
    ),
  )

  private lazy val isSnapshot: Boolean =
    sys.env.get("IS_SNAPSHOT").exists(_.toBoolean)

  private lazy val versionFromEnv: Option[String] =
    for {
      newVersion <- sys.env.get("NEW_VERSION")
      tagRaw = sys.env.get("LIBRARY_TAG")
      tag    = tagRaw.fold("")(r => s"-$r")
    } yield
      if (isSnapshot)
        newVersion ++ tag ++ "-SNAPSHOT"
      else
        newVersion

  private def applyVersionSuffix(versionBefore: String, currentCommitHash: String): String = {
    val snapshotSuffix = "-SNAPSHOT"
    if (isSnapshot && !versionBefore.contains(snapshotSuffix))
      versionBefore ++ currentCommitHash ++ snapshotSuffix
    else
      versionBefore
  }
}

object repos {
  val IbulRelease  = MavenRepository("Ibul Release", "https://nexus.tcsbank.ru/repository/ibul_release")
  val IbulSnapshot = MavenRepository("Ibul Snapshots", "https://nexus.tcsbank.ru/repository/ibul_snapshot")
}
