lazy val buildSettings = Seq(
  organization := "com.dwolla.sbt",
  name := "sbt-s3-publisher",
  homepage := Some(url("https://github.com/Dwolla/sbt-s3-publisher")),
  description := "SBT plugin to publish an assembled jar to S3",
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  sbtPlugin := true,
  crossSbtVersions := Vector("1.1.6"),
  startYear := Option(2016),
  resolvers += Resolver.bintrayRepo("dwolla", "maven"),
  addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.9.3"),
  addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.6"),
  libraryDependencies ++= {
    val awsSdkVersion = "1.11.277"
    val specs2Version = "3.8.6"

    Seq(
      "com.amazonaws" % "aws-java-sdk-s3" % awsSdkVersion,
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.dwolla" %% "scala-aws-utils" % "1.6.1",
      "org.specs2" %% "specs2-core" % specs2Version % Test,
      "org.specs2" %% "specs2-mock" % specs2Version % Test,
    )
  },
)

lazy val releaseSettings = {
  import ReleaseTransformations._
  import sbtrelease.Version.Bump._
  Seq(
    releaseVersionBump := Minor,
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      releaseStepCommandAndRemaining("^ test"),
      releaseStepCommandAndRemaining("^ scripted"),
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      releaseStepCommandAndRemaining("^ publish"),
      setNextVersion,
      commitNextVersion,
      pushChanges
    )
  )
}

lazy val bintraySettings = Seq(
  bintrayVcsUrl := Some("https://github.com/Dwolla/sbt-s3-publisher"),
  publishMavenStyle := false,
  bintrayRepository := "sbt-plugins",
  bintrayOrganization := Option("dwolla"),
  pomIncludeRepository := { _ ⇒ false }
)

scriptedLaunchOpts := { scriptedLaunchOpts.value ++
  Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
}

// uncomment to see sbt output for each scripted test run
//scriptedBufferLog := false

val s3PublisherPlugin = (project in file("."))
  .settings(buildSettings ++ bintraySettings ++ releaseSettings: _*)
