package models.meta

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import models.MonitorConfig
import models.dao.MonitorDb
import play.api.Logger

import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Play.current
import scala.concurrent.duration._
import models.agents.ActorSupervisor
import models.dao.Messages._

object Init {

  def init() = {

    val actorSystem = ActorSupervisor.getSystem
    val mailWatcherActor = ActorSupervisor.get("MailWatcher")
    mailWatcherActor ! InitMailWatcher("imap.gmail.com", "gbmonitor1@gmail.com", "PASS*123#")

    Logger.info("Initializing monitor db...")
    MonitorDb.createTables()
    MonitorDb.initTables()

    val CHECKS_HEARTBEAT = 10
    val EMAIL_HEARTBEAT = 10
    val CACHE_UPDATE_HEARTBEAT = 20

    Logger.info(s"Setting up monitor-db cache update scheduler with interval = $CACHE_UPDATE_HEARTBEAT seconds")
    actorSystem.scheduler.schedule(0 seconds, CACHE_UPDATE_HEARTBEAT seconds)(models.MonitorConfig.updateConfig)
    actorSystem.scheduler.schedule(0 seconds, CACHE_UPDATE_HEARTBEAT seconds)(models.meta.Cache.updateMpsList)
    actorSystem.scheduler.schedule(0 seconds, CACHE_UPDATE_HEARTBEAT seconds)(models.config.CustomerConfig.refreshCustomerConfig)
    actorSystem.scheduler.schedule(0 seconds, CACHE_UPDATE_HEARTBEAT seconds)(models.checks.Tasks.addCustChecksFromDefault)
    actorSystem.scheduler.schedule(0 seconds, CACHE_UPDATE_HEARTBEAT seconds)(models.clients.Tasks.updateClients)

    Logger.info(s"Setting up email scheduler with interval = $EMAIL_HEARTBEAT seconds")
    actorSystem.scheduler.schedule(20 seconds, EMAIL_HEARTBEAT seconds)(models.notification.SendMail.sendAllMails)

    Logger.info(s"Setting up checks scheduler with interval = $CHECKS_HEARTBEAT seconds")
    //actorSystem.scheduler.schedule(30 seconds, CHECKS_HEARTBEAT seconds)(models.checks.Schedule.runAllChecks)



  }


}

