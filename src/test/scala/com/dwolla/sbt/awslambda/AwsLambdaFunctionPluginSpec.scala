package com.dwolla.sbt.awslambda

import java.io.File

import com.amazonaws.services.s3.transfer.{TransferManager, Upload}
import com.dwolla.util.Environment
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import sbt.Keys.TaskStreams
import sbt.Logger

class AwsLambdaFunctionPluginSpec extends Specification with Mockito {

  class Setup(environment: (String, String)*) extends Scope {
    val testClass = new AwsLambdaFunctionPlugin(FakeEnvironment(Map(environment: _*)))
  }

  case class FakeEnvironment(map: Map[String, String]) extends Environment {
    override def get(name: String): Option[String] = map.get(name)
  }

  "s3BucketEnvironmentVariable" should {
    "default to DWOLLA_CODE_BUCKET" in new Setup {
      testClass.defaultBucketEnvironmentVariable must beSome("DWOLLA_CODE_BUCKET")
    }
  }

  "s3Bucket" should {
    "get value from environment if environment variable is set" in new Setup("defaultEnvironmentVariable" → "my-bucket") {
      testClass.s3Bucket(Option("defaultEnvironmentVariable"), "value") must_== "my-bucket"
    }

    "fall back to default if environment variable is not set" in new Setup {
      testClass.s3Bucket(Option("defaultEnvironmentVariable"), "default") must_== "default"
    }

    "fall back to default if no environment variable is to be used" in new Setup {
      testClass.s3Bucket(None, "default") must_== "default"
    }

    "use the correct default value" in new Setup {
      testClass.defaultS3Bucket must_== "dwolla-code-sandbox"
    }
  }

  "s3Prefix" should {
    "parameterize normalizedName and version and return the correct path prefix" in new Setup {
      testClass.s3Prefix("normalizedName", "version") must_== "lambdas/normalizedName/version"
    }
  }

  "s3Key" should {
    "parameterize s3Prefix and normalizedName and create a path to a jar file" in new Setup {
      testClass.s3Key("s3Prefix", "normalizedName") must_== "s3Prefix/normalizedName.jar"
    }
  }

  "s3TransferManager" should {
    "be an instance of the S3 Transfer Manager" in new Setup {
      testClass.s3TransferManager must beAnInstanceOf[TransferManager]
    }
  }

  "publish" should {
    "log intention, upload assembly, and log completion" in new Setup {
      val artifact = {
        val m = mock[File]
        m.getName returns "name"
        m
      }
      val log = mock[Logger]
      val streams = {
        val m = mock[TaskStreams]
        m.log returns log
        m
      }
      val upload = mock[Upload]
      val transferManager = {
        val m = mock[TransferManager]
        m.upload("bucket", "key", artifact) returns upload
        m
      }

      testClass.publish(artifact, "bucket", "key", streams, transferManager)

      there was one(log).info("Uploading «name» to «s3://bucket/key»")
      there was one(upload).waitForCompletion()
      there was one(log).info("Published to «s3://bucket/key»")
    }
  }
}
