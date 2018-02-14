package com.dwolla.sbt.awslambda

import com.typesafe.sbt.GitVersioning
import sbt.Keys._
import sbt._
import sbtassembly.AssemblyPlugin

import scala.language.{implicitConversions, postfixOps}

object PublishToS3 extends AutoPlugin {

  object autoImport extends S3PublisherPluginKeys

  import autoImport._
  import sbtassembly.AssemblyPlugin.autoImport._

  override def requires = GitVersioning && AssemblyPlugin

  lazy val plugin = new S3PublisherPlugin

  lazy val defaultValues = Seq(
    s3BucketEnvironmentVariable := plugin.defaultBucketEnvironmentVariable,
    s3TransferManager := plugin.s3TransferManager,
    defaultS3Bucket := plugin.defaultS3Bucket,
    uploadedArtifact := (assembly in assembly).value
  )

  lazy val tasks = Seq(
    s3Bucket := plugin.s3Bucket(s3BucketEnvironmentVariable.value, defaultS3Bucket.value),
    s3Prefix := plugin.s3Prefix(normalizedName.value, version.value, (assembly in assembly).value),
    s3Key := plugin.s3Key(s3Prefix.value, normalizedName.value),
    publish := plugin.publish(uploadedArtifact.value, s3Bucket.value, s3Key.value, streams.value.log, s3TransferManager.value)
  )

  lazy val publishToS3Settings = Defaults.itSettings ++ defaultValues ++ tasks

  override lazy val projectSettings = publishToS3Settings
}
