# Amazon S3 Publisher SBT Plugin

![Dwolla/sbt-s3-publisher CI](https://github.com/Dwolla/sbt-s3-publisher/actions/workflows/ci.yml/badge.svg)
[![license](https://img.shields.io/github/license/Dwolla/sbt-s3-publisher.svg?style=flat-square)]()

An sbt plugin to assemble and publish application jars to Amazon S3.

## Installation and Enabling

Add the following to `project/plugins.sbt`

```scala
addSbtPlugin("com.dwolla.sbt" % "sbt-s3-publisher" % "{version-number}")
```
Then, enable the plugin by adding something like the following to `build.sbt`:

```scala
val app = (project in file(".")).enablePlugins(PublishToS3)
```
## Included Plugins

- [sbt-git](https://github.com/sbt/sbt-git) plugin to set the version using the git hash
- [sbt-assembly]() to build the jar

## Settings

By default, the assembly artifact of the project using this plugin will be published to `s3://{s3Bucket}/{s3Key}`. The values are configured as follows:

### `s3Bucket`

`s3Bucket` is a task that checks for the presence of an environment variable, and uses its value if present. By default, this environment variable is `DWOLLA_CODE_BUCKET`, but it can be overridden by setting a different value in `s3BucketEnvironmentVariable`:

```scala
s3BucketEnvironmentVariable := Some("MY_ENV_VARIABLE")
```

If the environment variable is not set, or `s3BucketEnvironmentVariable` is set to `None`, then the default bucket `dwolla-code-sandbox` will be used.

The intention is to allow a build system to set a different, more restrictive bucket for artifacts published for production use, whereas developers would be able to publish from their local machines to an open bucket during development.

### `s3Key`

By default, `s3Key` will be set to `lambdas/{normalizedName}/{version}/{normalizedName}.jar`.

If there are uncommitted changes, or publishing from a branch whose git-ref does not match the `publishBranch` value (defaults to `master`), the SHA1 hash of the assembly JAR will be included in the path.

### `s3TransferManager`

If custom S3 transfer settings are needed (a different credential strategy, etc.), a `com.amazonaws.services.s3.transfer.TransferManager` can be provided using the `s3TransferManager` setting.

## Tasks

### `publish`

Assembles a jar and uploads it to the configured S3 path.
