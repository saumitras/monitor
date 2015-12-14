package models.agents

import akka.actor.Actor
import play.api.Logger

class LogCopier extends Actor {

  val LCP_LOGS_PATH = "~/lcp/current/logs/"
  def receive() = {
    case _ =>
      Logger.warn("Unknown message received")
  }

}
