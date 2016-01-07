package models.agents

import akka.actor.{Props, ActorRef, ActorSystem}
import akka.routing.SmallestMailboxPool
import com.typesafe.config.ConfigFactory
import models.dao.Messages._
import models.notification.MailWatcher
import akka.routing.{ ActorRefRoutee, RoundRobinRoutingLogic, Router }
import play.api.Logger

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
      case "S3UPLOADER" =>
        val a = system.actorOf(SmallestMailboxPool(4).props(Props[S3Uploader]),"S3Uploader")
        actorRefs += (name.toUpperCase -> a)
        a
      case "HEAPDUMPER" =>
        val a = system.actorOf(SmallestMailboxPool(1).props(Props[HeapDumpGenerator]),"HeapDumper")
        actorRefs += (name.toUpperCase -> a)
        a
      case "REMOTELISTENER" =>
        Logger.info("Creating remotelistener actor")
        val a = system.actorOf(Props[RemoteListener], "RemoteListener")
        actorRefs += (name.toUpperCase -> a)
        a
    }
  }


}
