package models.meta

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import models.Config
import models.dao.MonitorDb
import play.api.Logger

import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Play.current
import scala.concurrent.duration._

object Init {

  def init() = {

    //val actorSystem = models.agents.ActorSupervisor.monitorActorSystem

    Logger.info("Initializing monitor db...")
    MonitorDb.createTables()
    MonitorDb.initTables()

    val CHECKS_HEARTBEAT = 10
    Logger.info(s"Setting up checks scheduler with interval = $CHECKS_HEARTBEAT seconds")
    Akka.system.scheduler.schedule(0 seconds, CHECKS_HEARTBEAT seconds)(models.checks.Schedule.runAllChecks)

    val EMAIL_HEARTBEAT = 10
    Logger.info(s"Setting up email scheduler with interval = $EMAIL_HEARTBEAT seconds")
    Akka.system.scheduler.schedule(0 seconds, EMAIL_HEARTBEAT seconds)(models.notification.SendMail.sendAllMails)

  }


}

