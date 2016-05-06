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
    defaultS3Bucket := plugin.defaultS3Bucket
  )

  lazy val tasks = Seq(
    s3Bucket <<= (s3BucketEnvironmentVariable, defaultS3Bucket) map plugin.s3Bucket,
    s3Prefix <<= (normalizedName, version) map plugin.s3Prefix,
    s3Key <<= (s3Prefix, normalizedName) map plugin.s3Key,
    publish <<= (assembly in assembly, s3Bucket, s3Key, streams, s3TransferManager) map plugin.publish
  )

  lazy val awsLambdaFunctionPluginSettings = Defaults.itSettings ++ defaultValues ++ tasks

  override lazy val projectSettings = awsLambdaFunctionPluginSettings
}
