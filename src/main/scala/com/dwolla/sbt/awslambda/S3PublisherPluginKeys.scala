package com.dwolla.sbt.awslambda

import com.amazonaws.services.s3.transfer.TransferManager
import sbt._

import scala.language.postfixOps

trait S3PublisherPluginKeys {
  lazy val s3BucketEnvironmentVariable = SettingKey[Option[String]]("environment variable possibly holding an override to «defaultS3Bucket»")
  lazy val s3TransferManager = SettingKey[TransferManager]("S3 transfer manager")
  lazy val defaultS3Bucket = SettingKey[String]("defaultS3Bucket", "Default S3 bucket for artifact")
  lazy val s3Bucket = TaskKey[String]("s3Bucket", "S3 bucket for artifact. Uses «defaultS3Bucket», unless an environment variable overrides it")
  lazy val s3Prefix = TaskKey[String]("s3Prefix", "S3 prefix")
  lazy val s3Key = TaskKey[String]("s3Key", "S3 path in bucket")
}
