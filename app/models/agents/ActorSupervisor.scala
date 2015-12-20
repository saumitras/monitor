package models.agents

import akka.actor.{Props, ActorRef, ActorSystem}
import com.typesafe.config.ConfigFactory
import models.notification.MailWatcher

object ActorSupervisor {

  private val system = ActorSystem("GBMonitor", ConfigFactory.load("remote"))

  private var actorRefs = Map[String, ActorRef]()

  def getSystem:ActorSystem = system

  def get(name:String):ActorRef = {
    actorRefs.get(name.toUpperCase) match {
      case Some(a) => a
      case None =>
        val a = create(name)
        a
    }
  }

  def create(name:String):ActorRef = {
    name.toUpperCase match {
      case "MAILWATCHER" =>
        val a = system.actorOf(Props[MailWatcher], "MailWatcher")
        actorRefs += (name.toUpperCase -> a)
        a
    }
  }


}
