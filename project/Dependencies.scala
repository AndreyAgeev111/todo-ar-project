import DependenciesVersion.*
import sbt.*

object Dependencies {
  private val tapirDependencies: Seq[ModuleID] = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-sttp-stub-server" % tapirVersion % Test,
  )
  private val http4sDependencies: Seq[ModuleID] = Seq(
    "org.http4s" %% "http4s-ember-server" % http4sVersion
  )
  private val logbackDependencies: Seq[ModuleID] = Seq(
    "ch.qos.logback" % "logback-classic" % logbackVersion
  )
  private val scalaTestDependencies: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
    "org.scalatestplus" %% "mockito-4-11" % mockitoVersion % Test,
    "org.typelevel" %% "cats-effect-testing-scalatest" % scalaTestEffectVersion % Test
  )
  private val circeDependencies: Seq[ModuleID] = Seq(
    "com.softwaremill.sttp.client3" %% "circe" % circeVersion % Test
  )
  private val doobieDependencies: Seq[ModuleID] = Seq(
    "org.tpolecat" %% "doobie-core" % doobieVersion,
    "org.tpolecat" %% "doobie-h2" % doobieVersion,
    "org.tpolecat" %% "doobie-hikari" % doobieVersion,
    "org.tpolecat" %% "doobie-postgres" % doobieVersion,
    "org.tpolecat" %% "doobie-postgres-circe" % doobieVersion,
    "org.tpolecat" %% "doobie-specs2" % doobieVersion % Test,
    "org.tpolecat" %% "doobie-scalatest" % doobieVersion % Test
  )
  private val wireDependencies: Seq[ModuleID] = Seq(
    "com.softwaremill.macwire" %% "macros" % wireVersion % Provided,
    "com.softwaremill.macwire" %% "util" % wireVersion,
    "com.softwaremill.macwire" %% "proxy" % wireVersion
  )
  private val configDependencies: Seq[ModuleID] = Seq(
    "com.github.pureconfig" %% "pureconfig" % configVersion
  )
  final val allProjectDependencies: Seq[ModuleID] = tapirDependencies ++ http4sDependencies ++ logbackDependencies ++ scalaTestDependencies ++ circeDependencies ++ doobieDependencies ++ wireDependencies ++ configDependencies
}
