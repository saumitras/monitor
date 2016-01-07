package models.agents

object AgentsMsg {
  case class UploadToS3(isDir:Boolean, source:String, dest:String)
  case class HeapDump(source:String, eventId:String)
}
