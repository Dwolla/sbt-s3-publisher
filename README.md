# Amazon Web Services Lambda Function SBT Plugin

[![Travis](https://img.shields.io/travis/Dwolla/sbt-aws-lambda.svg?style=flat-square)](https://travis-ci.org/Dwolla/sbt-aws-lambda)
[![Bintray](https://img.shields.io/bintray/v/dwolla/sbt-plugins/aws-lambda.svg?style=flat-square)](https://bintray.com/dwolla/sbt-plugins/aws-lambda/view)
[![license](https://img.shields.io/github/license/Dwolla/sbt-aws-lambda.svg?style=flat-square)]()

An sbt plugin to publish and deploy Java 8-based AWS Lambda Functions.

## Installation and Enabling

Add the following to `project/plugins.sbt`

    addSbtPlugin("com.dwolla.sbt" % "aws-lambda" % "{version-number}")

Then, enable the plugin by adding something like the following to `build.sbt`:

    val app = (project in file(".")).enablePlugins(AwsLambdaFunction)

## Included Plugins

- [sbt-git](https://github.com/sbt/sbt-git) plugin to set the version using the git hash
- [sbt-assembly]() to build the jar composing the Lambda function.

## Settings

By default, the assembly artifact of the project using this plugin will be published to `s3://{s3Bucket}/{s3Key}`. The values are configured as follows:

### `s3Bucket`

`s3Bucket` is a task that checks for the presence of an environment variable, and uses its value if present. By default, this environment variable is `DWOLLA_CODE_BUCKET`, but it can be overridden by setting a different value in `s3BucketEnvironmentVariable`:

```
s3BucketEnvironmentVariable := Some("MY_ENV_VARIABLE")
```

If the environment variable is not set, or `s3BucketEnvironmentVariable` is set to `None`, then the default bucket `dwolla-code-sandbox` will be used.

The intention is to allow a build system to set a different, more restrictive bucket for artifacts published for production use, whereas developers would be able to publish from their local machines to an open bucket during development.

### `s3Key`

By default, `s3Key` will be set to `lambdas/{normalizedName}/{version}/{normalizedName}.jar`.

### `s3TransferManager`

If custom S3 transfer settings are needed (a different credential strategy, etc.), a `com.amazonaws.services.s3.transfer.TransferManager` can be provided using the `s3TransferManager` setting.

## Tasks

### `publish`

Assembles a JAR and uploads it to the configured S3 path.

### `stack/deploy`

Creates a CloudFormation stack that deploys the Lambda function.
