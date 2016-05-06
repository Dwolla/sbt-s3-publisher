import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Try

name := "s3 settings"
version := "v1"

lazy val fakeTransferManager = settingKey[FakeS3TransferManager]("")

fakeTransferManager := new FakeS3TransferManager
s3TransferManager := fakeTransferManager.value

val app = (project in file("."))
  .enablePlugins(PublishToS3)

TaskKey[Unit]("check") <<= (
  s3BucketEnvironmentVariable,
  defaultS3Bucket,
  s3Bucket,
  s3Prefix,
  s3Key,
  assembly in assembly,
  fakeTransferManager,
  publish
  ) map { (s3BucketEnvironmentVariable,
           defaultS3Bucket,
           s3Bucket,
           s3Prefix,
           s3Key,
           uploadedFile, fakeTransferManager, _) ⇒

  def err(key: String, expected: String, actual: String): Left[String, Unit] = Left(s"""$key: expected "$expected", got "$actual"""")

  val testResults: Seq[Either[String, Unit]] = Seq(
      if (s3BucketEnvironmentVariable == Some("DWOLLA_CODE_BUCKET")) Right(()) else err("s3BucketEnvironmentVariable", "DWOLLA_CODE_BUCKET", s3BucketEnvironmentVariable.getOrElse("None"))
    , if (defaultS3Bucket == "dwolla-code-sandbox") Right(()) else err("defaultS3Bucket", "dwolla-code-sandbox", defaultS3Bucket)
    , if (s3Bucket == "dwolla-code-sandbox") Right(()) else err("s3Bucket", "dwolla-code-sandbox", s3Bucket)
    , if (s3Prefix == "lambdas/s3-settings/v1") Right(()) else err("s3Prefix", "lambdas/s3-settings/v1", s3Prefix)
    , if (s3Key == "lambdas/s3-settings/v1/s3-settings.jar") Right(()) else err("s3Key", "lambdas/s3-settings/v1/s3-settings.jar", s3Key)
    , if (fakeTransferManager.uploads.contains(UploadRequest(s3Bucket, s3Key, uploadedFile))) Right(()) else Left(s"Uploads should have contained ${(s3Bucket, s3Key, uploadedFile)}, but did not.")
    , if (Try(Await.result(fakeTransferManager.uploadedCompleted.future, 1 second)).getOrElse(false)) Right(()) else Left("Upload should have waited for completion, but did not.")
  )

  val allErrors = testResults.filter(_.isLeft).map(_.left.get)
  if (allErrors.nonEmpty) sys.error(
    s"""${allErrors.size} failures detected:
       |
       |${allErrors.mkString("\n")}
     """.stripMargin)
}
