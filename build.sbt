inThisBuild(List(
  organization := "com.dwolla.sbt",
  description := "SBT plugin to publish an assembled jar to S3",
  sbtPlugin := true,
  startYear := Option(2016),
  homepage := Some(url("https://github.com/Dwolla/sbt-s3-publisher")),
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  developers := List(
    Developer(
      "bpholt",
      "Brian Holt",
      "bholt@dwolla.com",
      url("https://dwolla.com")
    )
  ),
  addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "1.0.0"),
  addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.15.0"),

  githubWorkflowJavaVersions := Seq("adopt@1.8", "adopt@1.11"),
  githubWorkflowTargetTags ++= Seq("v*"),
  githubWorkflowPublishTargetBranches :=
    Seq(RefPredicate.StartsWith(Ref.Tag("v"))),
  githubWorkflowBuild := Seq(WorkflowStep.Sbt(List("test", "scripted"), name = Some("Build and test project"))),
  githubWorkflowPublish := Seq(WorkflowStep.Sbt(List("ci-release"))),
  githubWorkflowPublish := Seq(
    WorkflowStep.Sbt(
      List("ci-release"),
      env = Map(
        "PGP_PASSPHRASE" -> "${{ secrets.PGP_PASSPHRASE }}",
        "PGP_SECRET" -> "${{ secrets.PGP_SECRET }}",
        "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
        "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
      )
    )
  ),
))

scriptedLaunchOpts ++= Seq("-Xmx1024M", "-Dplugin.version=" + version.value)

// uncomment to see sbt output for each scripted test run
//scriptedBufferLog := false

val `sbt-s3-publisher` = (project in file("."))
  .settings(
    sonatypeProfileName := "com.dwolla",
    libraryDependencies ++= {
      val awsSdkVersion = "1.11.996"
      val specs2Version = "4.10.6"

      Seq(
        "com.amazonaws" % "aws-java-sdk-s3" % awsSdkVersion,
        "ch.qos.logback" % "logback-classic" % "1.2.3",
        "com.dwolla" %% "scala-aws-utils" % "1.6.2",
        "org.specs2" %% "specs2-core" % specs2Version % Test,
        "org.specs2" %% "specs2-mock" % specs2Version % Test,
      )
    },
  )
  .enablePlugins(SbtPlugin)
