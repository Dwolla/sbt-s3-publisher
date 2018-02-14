package com.dwolla.sbt.awslambda

import java.io.File

import com.amazonaws.services.s3.transfer._
import com.dwolla.sbt.s3.model.VersionedArtifact
import com.dwolla.util._
import xsbti.Logger

class S3PublisherPlugin(environment: Environment = SystemEnvironment) {
  import com.dwolla.sbt.util.LoggerImplicits._
  val defaultS3Bucket = "dwolla-code-sandbox"
  val defaultBucketEnvironmentVariable = Option("DWOLLA_CODE_BUCKET")

  def publish(artifact: File, bucket: String, s3Key: String, log: Logger, transferManager: TransferManager) = {
    log.info(s"Uploading «${artifact.getName}» to «s3://$bucket/$s3Key»")
    transferManager.upload(bucket, s3Key, artifact).waitForCompletion()
    log.info(s"Published to «s3://$bucket/$s3Key»")
  }

  def s3TransferManager: TransferManager = TransferManagerBuilder.defaultTransferManager()

  def s3Key(s3Prefix: String, normalizedName: String) = s"$s3Prefix/$normalizedName.jar"

  def s3Prefix(normalizedName: String, version: String, versionedArtifact: VersionedArtifact) =
    s"lambdas/$normalizedName/$version${versionedArtifact.s3PathSuffix}"

  def s3Bucket(envVariable: Option[String], defaultBucketName: String): String = envVariable.flatMap(environment.get).getOrElse(defaultBucketName)
}
