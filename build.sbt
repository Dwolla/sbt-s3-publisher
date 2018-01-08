lazy val buildSettings = Seq(
  organization := "com.dwolla.sbt",
  name := "sbt-s3-publisher",
  homepage := Some(url("https://github.com/Dwolla/sbt-s3-publisher")),
  description := "SBT plugin to publish an assembled jar to S3",
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  sbtPlugin := true,
  crossSbtVersions := Vector("1.1.0", "1.0.4", "0.13.16"),
  startYear := Option(2016),
  resolvers += Resolver.bintrayIvyRepo("dwolla", "maven"),
  libraryDependencies ++= {
    import sbt.Defaults.sbtPluginExtra
    val awsSdkVersion = "1.11.172"
    val specs2Version = "3.8.6"
    val currentSbtVersion = (sbtBinaryVersion in pluginCrossBuild).value

    Seq(
      "com.amazonaws" % "aws-java-sdk-s3" % awsSdkVersion,
      "com.amazonaws" % "aws-java-sdk-cloudformation" % awsSdkVersion,
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.dwolla" %% "scala-aws-utils" % "1.5.0",
      "org.specs2" %% "specs2-core" % specs2Version % "test",
      "org.specs2" %% "specs2-mock" % specs2Version % "test",

      // https://github.com/sbt/sbt/issues/3393
      sbtPluginExtra("com.typesafe.sbt" % "sbt-git" % "0.9.3", currentSbtVersion, scalaBinaryVersion.value),
      sbtPluginExtra("com.eed3si9n" % "sbt-assembly" % "0.14.5", currentSbtVersion, scalaBinaryVersion.value)
    )
  },
  releaseVersionBump := sbtrelease.Version.Bump.Minor,
  releaseProcess --= {
    import ReleaseTransformations._
    Seq(runClean, runTest, publishArtifacts)
  }
)

lazy val bintraySettings = Seq(
  bintrayVcsUrl := Some("https://github.com/Dwolla/sbt-s3-publisher"),
  publishMavenStyle := false,
  bintrayRepository := "sbt-plugins",
  bintrayOrganization := Option("dwolla"),
  pomIncludeRepository := { _ ⇒ false }
)

lazy val pipeline = InputKey[Unit]("pipeline", "Runs the full build pipeline: compile, test, integration tests")
pipeline := scripted.dependsOn(test in Test).evaluated

scriptedLaunchOpts := { scriptedLaunchOpts.value ++
  Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
}

// uncomment to see sbt output for each scripted test run
//scriptedBufferLog := false

val s3PublisherPlugin = (project in file("."))
  .settings(buildSettings ++ bintraySettings: _*)
  .settings(ScriptedPlugin.scriptedSettings: _*)
