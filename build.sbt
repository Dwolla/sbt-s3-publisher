import java.lang.System._

lazy val buildVersion = {
  val mainVersion = "0.1"
  val minorVersion = Option(getenv("TRAVIS_BUILD_NUMBER"))
  minorVersion match {
    case Some(v: String) ⇒ s"$mainVersion.$v"
    case None ⇒ mainVersion + "-SNAPSHOT"
  }
}

lazy val buildSettings = Seq(
  organization := "com.dwolla.sbt",
  name := "aws-lambda",
  homepage := Some(url("https://github.com/Dwolla/sbt-aws-lambda")),
  description := "SBT plugin to deploy AWS Lambda functions using CloudFormation",
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  version := buildVersion,
  scalaVersion := "2.10.6",
  sbtPlugin := true,
  startYear := Option(2016),
  resolvers += Resolver.bintrayIvyRepo("dwolla", "maven"),
  libraryDependencies ++= {
    val awsSdkVersion = "1.10.71"
    val specs2Version = "3.7.2"
    Seq(
      "com.amazonaws"   %  "aws-java-sdk-s3"              % awsSdkVersion,
      "com.amazonaws"   %  "aws-java-sdk-cloudformation"  % awsSdkVersion,
      "ch.qos.logback"  %  "logback-classic"              % "1.1.7",
      "com.dwolla"      %% "scala-aws-utils"              % "0.1.6",
      "org.specs2"      %% "specs2-core"                  % specs2Version  % "test",
      "org.specs2"      %% "specs2-mock"                  % specs2Version  % "test"
    )
  }
)

lazy val bintraySettings = Seq(
  bintrayVcsUrl := Some("https://github.com/Dwolla/sbt-aws-lambda"),
  publishMavenStyle := false,
  bintrayRepository := "sbt-plugins",
  bintrayOrganization := Option("dwolla"),
  pomIncludeRepository := { _ ⇒ false }
)

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.8.5")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.12.0")

val awsLambdaPlugin = (project in file("."))
  .settings(buildSettings ++ bintraySettings: _*)
