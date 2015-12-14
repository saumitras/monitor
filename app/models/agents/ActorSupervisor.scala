package models.agents

import akka.actor.{ActorRef, ActorSystem}
import com.typesafe.config.ConfigFactory

object ActorSupervisor {

  lazy final val monitorActorSystem = ActorSystem("GBMonitor", ConfigFactory.load("remote"))

  var actorRefs = Map[String, ActorRef]()

  def getActor(name:String) = {
    if(actorRefs.contains())
  }


}
