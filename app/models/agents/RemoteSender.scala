package models.agents

import akka.actor.Actor
import akka.actor.Actor.Receive
import models.dao.Messages.ActivateAgent

class RemoteSender extends Actor {
  override def receive: Receive = {
    case ActivateAgent(eventId:String) =>
      val remote = context.actorSelection("akka.tcp://GBMonitor@127.0.0.1:7390/user/RemoteListener")
      remote ! s"$eventId,GET_LCP_HEAPDUMP|GET_LCP_LOG"

  }
}
