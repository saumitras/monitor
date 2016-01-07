package models.agents

import akka.actor.Actor
import models.agents.AgentsMsg.{HeapDump, UploadToS3}
import play.api.Logger
import scala.sys.process._

class RemoteListener extends Actor {

  Logger.info("Init RemoteListener Actor")

  def receive = {
    case s:String =>
      Logger.info("Received remote message " + s)
      val data = s.split(",").filter(_.nonEmpty)
      if(data.size == 2) {
        val eventId = data(0)
        if(isValidEventId(eventId)) {
          val commands = data(1).split("\\|").filter(_.nonEmpty)
          Logger.info(s"eventId = $eventId")
          Logger.info(s"commands = $commands")

          for(cmd <- commands) {
            cmd match {
              case "GET_LCP_LOG" =>
                getLcpLogs(eventId)
              case "GET_LCP_HEAPDUMP" =>
                generateLcpHeapDump(eventId)
              case "KILL_LCP" =>
                killLcp(eventId)
            }
          }
        }
      }

    case _ =>
      Logger.warn("Unknown message received in RemoteListener")
  }

  def isValidEventId(s:String):Boolean = s forall Character.isDigit

  def getLcpLogs(eventId:String) = {
    val source = Constants.monitorLogsPath +  eventId + "/lcplogs/" + Constants.NODE_IP + "/"
    val dest = Constants.S3_BASE_PATH + eventId + "/lcplogs/" + Constants.NODE_IP + "/"

    if(ShellCmd.copyLcpLogs(source)) {
      val s3Actor = ActorSupervisor.get("S3UPLOADER")
      s3Actor ! UploadToS3(true, source, dest)
    }
  }


  def generateLcpHeapDump(eventId:String) = {
    val actor = ActorSupervisor.get("HEAPDUMPER")
    actor ! HeapDump("lcp", eventId)
  }


  def killLcp(eventId:String) = {
    ShellCmd.killProcessByName(Constants.LCP_PROCESS_NAME)
  }



}
