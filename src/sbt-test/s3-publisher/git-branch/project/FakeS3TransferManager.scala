import java.io.{File, InputStream}
import java.util
import java.util.Date

import com.amazonaws.AmazonClientException
import com.amazonaws.event.ProgressListener
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model._
import com.amazonaws.services.s3.transfer.Transfer.TransferState
import com.amazonaws.services.s3.transfer.internal.{S3ProgressListener, TransferStateChangeListener}
import com.amazonaws.services.s3.transfer._
import com.amazonaws.services.s3.transfer.model.UploadResult

import scala.concurrent.Promise

class FakeS3TransferManager extends UnimplementedTransferManager {
  val uploads = scala.collection.mutable.Buffer.empty[UploadRequest]
  val uploadedCompleted = Promise[Boolean]

  override def upload(bucketName: String,
                      key: String,
                      file: File): Upload = {
    uploads += UploadRequest(bucketName, key, file)
    new FakeUpload(uploadedCompleted)
  }
}

class FakeUpload(uploadCompletedPromise: Promise[Boolean]) extends UnimplementedUpload {
  override def waitForCompletion(): Unit = uploadCompletedPromise.success(true)
}

case class UploadRequest(bucketName: String, key: String, file: File)

class UnimplementedTransferManager extends TransferManager {
  override def upload(bucketName: String, key: String, file: File): Upload = ???
  override def uploadFileList(bucketName: String, virtualDirectoryKeyPrefix: String, directory: File, files: util.List[File]): MultipleFileUpload = ???
  override def uploadFileList(bucketName: String, virtualDirectoryKeyPrefix: String, directory: File, files: util.List[File], metadataProvider: ObjectMetadataProvider): MultipleFileUpload = ???
  override def resumeDownload(persistableDownload: PersistableDownload): Download = ???
  override def shutdownNow(): Unit = ???
  override def shutdownNow(shutDownS3Client: Boolean): Unit = ???
  override def getAmazonS3Client: AmazonS3 = ???
  override def finalize(): Unit = ???
  override def setConfiguration(configuration: TransferManagerConfiguration): Unit = ???
  override def resumeUpload(persistableUpload: PersistableUpload): Upload = ???
  override def upload(bucketName: String, key: String, input: InputStream, objectMetadata: ObjectMetadata): Upload = ???
  override def upload(putObjectRequest: PutObjectRequest): Upload = ???
  override def upload(putObjectRequest: PutObjectRequest, progressListener: S3ProgressListener): Upload = ???
  override def copy(sourceBucketName: String, sourceKey: String, destinationBucketName: String, destinationKey: String): Copy = ???
  override def copy(copyObjectRequest: CopyObjectRequest): Copy = ???
  override def copy(copyObjectRequest: CopyObjectRequest, stateChangeListener: TransferStateChangeListener): Copy = ???
  override def abortMultipartUploads(bucketName: String, date: Date): Unit = ???
  override def download(bucket: String, key: String, file: File): Download = ???
  override def download(bucket: String, key: String, file: File, timeoutMillis: Long): Download = ???
  override def download(getObjectRequest: GetObjectRequest, file: File): Download = ???
  override def download(getObjectRequest: GetObjectRequest, file: File, timeoutMillis: Long): Download = ???
  override def download(getObjectRequest: GetObjectRequest, file: File, progressListener: S3ProgressListener): Download = ???
  override def download(getObjectRequest: GetObjectRequest, file: File, progressListener: S3ProgressListener, timeoutMillis: Long): Download = ???
  override def uploadDirectory(bucketName: String, virtualDirectoryKeyPrefix: String, directory: File, includeSubdirectories: Boolean): MultipleFileUpload = ???
  override def uploadDirectory(bucketName: String, virtualDirectoryKeyPrefix: String, directory: File, includeSubdirectories: Boolean, metadataProvider: ObjectMetadataProvider): MultipleFileUpload = ???
  override def downloadDirectory(bucketName: String, keyPrefix: String, destinationDirectory: File): MultipleFileDownload = ???
  override def getConfiguration: TransferManagerConfiguration = ???
}

class UnimplementedUpload extends Upload {
  override def pause(): PersistableUpload = ???
  override def abort(): Unit = ???
  override def waitForUploadResult(): UploadResult = ???
  override def tryPause(forceCancelTransfers: Boolean): PauseResult[PersistableUpload] = ???
  override def addProgressListener(listener: ProgressListener): Unit = ???
  override def addProgressListener(x$1: com.amazonaws.services.s3.model.ProgressListener): Unit = ???
  override def getProgress: TransferProgress = ???
  override def waitForException(): AmazonClientException = ???
  override def isDone: Boolean = ???
  override def removeProgressListener(listener: ProgressListener): Unit = ???
  override def removeProgressListener(x$1: com.amazonaws.services.s3.model.ProgressListener): Unit = ???
  override def getDescription: String = ???
  override def getState: TransferState = ???
  override def waitForCompletion(): Unit = ???
}
