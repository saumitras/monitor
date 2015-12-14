package models.dao

import java.sql.Timestamp

object Messages {
  case class DefaultCheck(cid:String, mps:String, description:String, interval:String, critical_threshold:String,
                          warning_threshold:String, threshold_unit:String, wait_duration:String, status:String)
  case class Check(id:Option[Long], cid:String, mps:String, description:String, interval:String,
                   critical_threshold:String, warning_threshold:String, threshold_unit:String,
                   wait_duration:String, status:String)
  case class FileStuckInSeen(mps:String, loadId:Long, node:String, ts:Timestamp, obs_ts:Timestamp,
                             seen:Timestamp, fileType:Byte, name:String)
  case class FileStuckInParse(mps:String, loadId:Long, node:String, ts:Timestamp, obs_ts:Timestamp,
                              seen:Timestamp, started:Timestamp, parser:Option[String], fileType:Byte,
                              name:String)
  case class LCPEvent(id:Option[Long],parentCheckId:Long, sourceCheckId:String, signature:String, status:String, name:String, mps:String, h2:String, loadId:String, source:String,
                      occurredAt:Timestamp, owner:String, escalationLevel:String, bug:String, component:String,
                      closedAt:Timestamp, resolution:String, kb:String)
  case class Client(name:String, group:String, status:String, health:String, stashedMps:String, stashDuration:Long)
  case class EmailEvent(id:Option[Long], eventId:Long, mps:String, title:String, recipient:String, sentCount:Int, body:String )
  case class EmailOps(id:Option[Long], group:String, title:String, recipient:String, sentCount:Int, body:String)



}


