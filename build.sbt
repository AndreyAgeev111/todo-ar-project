import Dependencies.allProjectDependencies

lazy val rootProject = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .settings(
    Seq(
      name := "todo-ar-project",
      version := "1.0.0",
      organization := "open.api",
      scalaVersion := "2.13.10",
      dockerBaseImage := "openjdk:11.0.7",
      dockerExposedPorts := Seq(9000),
      libraryDependencies ++= allProjectDependencies
    )
  )
