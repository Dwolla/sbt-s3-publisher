logLevel := Level.Warn

libraryDependencies <+= sbtVersion { sv ⇒
  "org.scala-sbt" % "scripted-plugin" % sv
}

addSbtPlugin("org.foundweekends" % "sbt-bintray" % "0.5.1")
