import Dependencies.allProjectDependencies

lazy val rootProject = (project in file(".")).settings(
  Seq(
    name := "todo-ar-project",
    version := "0.1.0-SNAPSHOT",
    organization := "open.api",
    scalaVersion := "2.13.10",
    libraryDependencies ++= allProjectDependencies
  )
)
