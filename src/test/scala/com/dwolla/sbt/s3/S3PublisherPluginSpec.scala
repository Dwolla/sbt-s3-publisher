package com.dwolla.sbt.s3

import java.io.File

import com.amazonaws.services.s3.transfer.{TransferManager, Upload}
import com.dwolla.sbt.s3.model._
import com.dwolla.sbt.testutils.LoggerCaptor._
import com.dwolla.util.Environment
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import xsbti.Logger

import scala.collection.JavaConverters._

class S3PublisherPluginSpec extends Specification with Mockito {

  class Setup(environment: (String, String)*) extends Scope {
    val mockFile = mock[File]
    val hashingStrategy: File ⇒ String = file ⇒ if (file == mockFile) "sha1" else "the wrong file was passed into the SHA1 function"
    val testClass = new S3PublisherPlugin(FakeEnvironment(Map(environment: _*)))
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
      testClass.s3Prefix("normalizedName", "version", VersionedArtifact(mockFile, isSnapshot = false)) must_== "lambdas/normalizedName/version"
    }

    "include the assembly jar's sha1 in the prefix if the version is a snapshot" in new Setup {
      testClass.s3Prefix("normalizedName", "version", SnapshotArtifact(mockFile, hashingStrategy)) must_== "lambdas/normalizedName/version/sha1"
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
      val upload = mock[Upload]
      val transferManager = {
        val m = mock[TransferManager]
        m.upload("bucket", "key", artifact) returns upload
        m
      }

      val logCaptor = loggerCapture()

      testClass.publish(artifact, "bucket", "key", log, transferManager)

      there were two(log).info(logCaptor.capture)
      there was one(upload).waitForCompletion()

      logCaptor.values.asScala.map(_.get()) must_== Seq("Uploading «name» to «s3://bucket/key»", "Published to «s3://bucket/key»")
    }
  }
}
