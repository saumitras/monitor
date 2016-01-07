package models.agents

import java.io.File
import akka.actor.Actor
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.s3.transfer.TransferManager
import models.agents.AgentsMsg._

class S3Uploader extends Actor {

  private val tm = new TransferManager(new ProfileCredentialsProvider())

  override def receive = {
    case UploadToS3(isDir, source, dest) =>
      if(! uploadLimitExceeded())
        upload(isDir, source, dest)
  }

  def uploadLimitExceeded():Boolean = {
    //TODO: capture uploaded size in last 24 hours and reject new upload request if it exceeds N MB
    false
  }


  def upload(isDir:Boolean, source:String, dest:String) = {
    println(s"Uploading. \nsource=$source\ndest=$dest")
    try {
      val upload = if(isDir)
        tm.uploadDirectory(Constants.S3_BUCKET, dest, new File(source), true)
      else
        tm.upload(Constants.S3_BUCKET, dest, new File(source))

      println(upload.getProgress.getPercentTransferred)

      upload.waitForCompletion()

    } catch {
      case ex:Exception =>
        println(ex.getStackTrace)
    }
    println(s"Uploaded $dest")
  }
}
