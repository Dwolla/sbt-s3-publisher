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

TaskKey[Unit]("check") := {
  val s3BucketEnvironmentVariableValue = s3BucketEnvironmentVariable.value
  val defaultS3BucketValue = defaultS3Bucket.value
  val s3BucketValue = s3Bucket.value
  val s3PrefixValue = s3Prefix.value
  val s3KeyValue = s3Key.value
  val uploadedFile = (assembly in assembly).value
  val fakeTransferManagerValue = fakeTransferManager.value
  publish.value

  def err(key: String, expected: String, actual: String): Left[String, Unit] = Left(s"""$key: expected "$expected", got "$actual"""")

  val testResults: Seq[Either[String, Unit]] = Seq(
      if (s3BucketEnvironmentVariableValue == Some("DWOLLA_CODE_BUCKET")) Right(()) else err("s3BucketEnvironmentVariable", "DWOLLA_CODE_BUCKET", s3BucketEnvironmentVariableValue.getOrElse("None"))
    , if (defaultS3BucketValue == "dwolla-code-sandbox") Right(()) else err("defaultS3Bucket", "dwolla-code-sandbox", defaultS3BucketValue)
    , if (s3BucketValue == "dwolla-code-sandbox") Right(()) else err("s3Bucket", "dwolla-code-sandbox", s3BucketValue)
    , if (s3PrefixValue == "lambdas/s3-settings/v1") Right(()) else err("s3Prefix", "lambdas/s3-settings/v1", s3PrefixValue)
    , if (s3KeyValue == "lambdas/s3-settings/v1/s3-settings.jar") Right(()) else err("s3Key", "lambdas/s3-settings/v1/s3-settings.jar", s3KeyValue)
    , if (fakeTransferManagerValue.uploads.contains(UploadRequest(s3BucketValue, s3KeyValue, uploadedFile))) Right(()) else Left(s"Uploads should have contained ${(s3BucketValue, s3KeyValue, uploadedFile)}, but did not.")
    , if (Try(Await.result(fakeTransferManagerValue.uploadedCompleted.future, 1 second)).getOrElse(false)) Right(()) else Left("Upload should have waited for completion, but did not.")
  )

  val allErrors = testResults.filter(_.isLeft).map(_.left.get)
  if (allErrors.nonEmpty) sys.error(
    s"""${allErrors.size} failures detected:
       |
       |${allErrors.mkString("\n")}
     """.stripMargin)
}
