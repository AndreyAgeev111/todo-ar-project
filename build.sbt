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
      dockerBaseImage := "openjdk:18.0.2",
      dockerExposedPorts ++= Seq(8080),
      libraryDependencies ++= allProjectDependencies
    )
  )
