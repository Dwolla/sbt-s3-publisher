import com.typesafe.sbt.git.GitRunner
import com.typesafe.sbt.SbtGit.git

class FakeGitRunner extends GitRunner {

  override def apply(args: String*)(cwd: sbt.File, log: sbt.Logger): String = "fake-123"

}