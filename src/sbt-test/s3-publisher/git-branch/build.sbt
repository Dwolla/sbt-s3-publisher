import scala.language.postfixOps

name := "git branch"
version := "v1"

lazy val fakeGitRunner = settingKey[FakeGitRunner]("")

fakeGitRunner := new FakeGitRunner

// Fake S3 manager to not actually publish during tests
s3TransferManager := new FakeS3TransferManager

// Verify that when checking out a branch that doesn't match `publishBranch`, if that branch's git-ref matches
// `publishBranch`'s git-ref, we still assume it's not a snapshot build
git.gitCurrentBranch := "not-master"
git.gitUncommittedChanges := false
git.gitHeadCommit := Some("fake-123")
git.runner := fakeGitRunner.value

val app = (project in file("."))
  .enablePlugins(PublishToS3)

TaskKey[Unit]("check") := {
  val s3PublishSnapshotValue = s3PublishSnapshot.value
  publish.value

  def err(key: String, expected: String, actual: String): Left[String, Unit] = Left(s"""$key: expected "$expected", got "$actual"""")

  val testResults: Seq[Either[String, Unit]] = Seq(
      if (!s3PublishSnapshotValue) Right(()) else err("s3PublishSnapshot", false.toString, s3PublishSnapshotValue.toString)
  )

  val allErrors = testResults.filter(_.isLeft).map(_.left.get)
  if (allErrors.nonEmpty) sys.error(
    s"""${allErrors.size} failures detected:
       |
       |${allErrors.mkString("\n")}
     """.stripMargin)
}
