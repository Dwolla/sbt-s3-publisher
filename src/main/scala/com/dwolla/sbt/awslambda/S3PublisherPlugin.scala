package com.dwolla.sbt.awslambda

import java.io.File

import com.amazonaws.services.s3.transfer.TransferManager
import com.dwolla.util.{Environment, SystemEnvironment}
import sbt.Keys.TaskStreams

class S3PublisherPlugin(environment: Environment = SystemEnvironment) {
  val defaultS3Bucket = "dwolla-code-sandbox"
  val defaultBucketEnvironmentVariable = Option("DWOLLA_CODE_BUCKET")

  def publish(artifact: File, bucket: String, s3Key: String, streams: TaskStreams, transferManager: TransferManager) = {
    streams.log.info(s"Uploading «${artifact.getName}» to «s3://$bucket/$s3Key»")
    transferManager.upload(bucket, s3Key, artifact).waitForCompletion()
    streams.log.info(s"Published to «s3://$bucket/$s3Key»")
  }

  def s3TransferManager = new TransferManager()

  def s3Key(s3Prefix: String, normalizedName: String) = s"$s3Prefix/$normalizedName.jar"

  def s3Prefix(normalizedName: String, version: String) = s"lambdas/$normalizedName/$version"

  def s3Bucket(envVariable: Option[String], defaultBucketName: String): String = envVariable.flatMap(environment.get).getOrElse(defaultBucketName)
}
