package com.dwolla.sbt.awslambda

import com.amazonaws.services.s3.transfer.TransferManager
import sbt._

import scala.language.postfixOps

trait S3PublisherPluginKeys {
  lazy val s3BucketEnvironmentVariable = settingKey[Option[String]]("environment variable possibly holding an override to «defaultS3Bucket»")
  lazy val s3TransferManager = settingKey[TransferManager]("S3 transfer manager")
  lazy val defaultS3Bucket = settingKey[String]("Default S3 bucket for artifact")
  lazy val s3Bucket = taskKey[String]("S3 bucket for artifact. Uses «defaultS3Bucket», unless an environment variable overrides it")
  lazy val s3Prefix = taskKey[String]("S3 prefix")
  lazy val s3Key = taskKey[String]("S3 path in bucket")
}
