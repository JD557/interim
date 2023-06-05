ThisBuild / organization       := "eu.joaocosta"
ThisBuild / scalaVersion       := "3.3.0"
ThisBuild / licenses           := Seq("MIT License" -> url("http://opensource.org/licenses/MIT"))
ThisBuild / homepage           := Some(url("https://github.com/JD557/guila"))
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/JD557/guila"),
    "scm:git@github.com:JD557/guila.git"
  )
)
ThisBuild / versionScheme := Some("semver-spec")
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

def docSettings(projectName: String) = Seq(
  Compile / doc / scalacOptions ++= (
    if (scalaBinaryVersion.value.startsWith("3"))
      Seq(
        "-project",
        projectName,
        "-project-version",
        version.value,
        "-social-links:" +
          "github::https://github.com/JD557/Minart"
      )
    else Seq()
  )
)

lazy val core = (projectMatrix in file("core"))
  .settings(
    name := "guila",
    Compile / doc / scalacOptions ++=
      Seq(
        "-project",
        "GUIla",
        "-project-version",
        version.value,
        "-social-links:github::https://github.com/JD557/guila"
      )
  )
  .jvmPlatform(scalaVersions = Seq("3.3.0"))
  .jsPlatform(scalaVersions = Seq("3.3.0"))
  .nativePlatform(scalaVersions = Seq("3.3.0"))
