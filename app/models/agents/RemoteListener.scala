package models.agents

import akka.actor.Actor
import play.api.Logger

class RemoteListener extends Actor {

  def receive() = {
    case msg:String =>
      val Array(eventId:String, cmd:String) = msg.split(",")
      cmd match {
        case "COPY_LCP_LOG" =>
          Logger.info(s"Received COPY_LCP_LOG request for eventId=$eventId")
        case "COPY_LCP_LOG_TO_S3" =>
        case "COPY_LCP_HEAPDUMP" =>
        case "COPY_LCP_HEAPDUMP_TO_S3" =>
        case "FULL_DEBUG_BUNDLE" =>
      }
    case _ =>
      Logger.warn("Unknown message received")
  }

}
