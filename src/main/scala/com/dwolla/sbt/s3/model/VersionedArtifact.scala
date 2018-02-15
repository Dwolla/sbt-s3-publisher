package com.dwolla.sbt.s3.model

import java.io.File

import sbt.Hash
import sbt.Hash.toHex

object VersionedArtifact {
  def apply(file: File, isSnapshot: Boolean): VersionedArtifact = if (isSnapshot) SnapshotArtifact(file) else ReleasedArtifact
}
sealed trait VersionedArtifact {
  def s3PathSuffix: String
}
case class SnapshotArtifact(artifact: File, hashingStrategy: File ⇒ String = f ⇒ toHex(Hash(f))) extends VersionedArtifact {
  override lazy val s3PathSuffix: String = s"/${hashingStrategy(artifact)}"
}
case object ReleasedArtifact extends VersionedArtifact {
  override val s3PathSuffix: String = ""
}
