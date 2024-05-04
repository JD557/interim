import ReleaseTransformations._

ThisBuild / organization := "eu.joaocosta"
ThisBuild / publishTo    := sonatypePublishToBundle.value
ThisBuild / scalaVersion := "3.3.3"
ThisBuild / licenses     := Seq("MIT License" -> url("http://opensource.org/licenses/MIT"))
ThisBuild / homepage     := Some(url("https://github.com/JD557/interim"))
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/JD557/interim"),
    "scm:git@github.com:JD557/interim.git"
  )
)
ThisBuild / versionScheme   := Some("semver-spec")
ThisBuild / autoAPIMappings := true
ThisBuild / scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-language:higherKinds",
  "-unchecked"
)
ThisBuild / scalafmtOnCompile := true
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision
ThisBuild / scalafixOnCompile := true

// Don't publish the root project
publish / skip  := true
publish         := (())
publishLocal    := (())
publishArtifact := false
publishTo       := None

val siteSettings = Seq(
  Compile / doc / scalacOptions ++= (
    if (scalaBinaryVersion.value.startsWith("3"))
      Seq("-siteroot", "docs")
    else Seq()
  )
)

lazy val core =
  crossProject(JVMPlatform, JSPlatform, NativePlatform)
    .in(file("core"))
    .settings(
      name                                    := "interim",
      libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0-RC1" % Test,
      Compile / doc / scalacOptions ++=
        Seq(
          "-project",
          "InterIm",
          "-project-version",
          version.value,
          "-social-links:github::https://github.com/JD557/interim",
          "-siteroot",
          "docs"
        )
    )

releaseCrossBuild    := true
releaseTagComment    := s"Release ${(ThisBuild / version).value}"
releaseCommitMessage := s"Set version to ${(ThisBuild / version).value}"

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("publishSigned"),
  releaseStepCommand("sonatypeBundleRelease"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)
