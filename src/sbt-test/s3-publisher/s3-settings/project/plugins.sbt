{
  val pluginVersion = System.getProperty("plugin.version")
  if(pluginVersion == null)
    throw new RuntimeException("""|The system property 'plugin.version' is not defined.
                                 |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
  else addSbtPlugin("com.dwolla.sbt" % "sbt-s3-publisher" % pluginVersion)
}

libraryDependencies += "com.amazonaws" % "aws-java-sdk-s3" % "1.11.372"
