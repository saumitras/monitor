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

    val CACHE_UPDATE_HEARTBEAT = 10
    val EMAIL_HEARTBEAT = 15
    val CHECKS_HEARTBEAT = 10000
    val EMAIL_WATCHER_POLLING_DURATION = 15

    val actorSystem = ActorSupervisor.getSystem
    val mailWatcherActor = ActorSupervisor.get("MailWatcher")
    mailWatcherActor ! InitMailWatcher("imap.gmail.com", "gbmonitor1@gmail.com", "PASS*123#", EMAIL_WATCHER_POLLING_DURATION)

    Logger.info("Initializing monitor db...")
    MonitorDb.createTables()
    MonitorDb.initTables()

    Logger.info(s"Setting up monitor-db cache update scheduler with interval = $CACHE_UPDATE_HEARTBEAT seconds")
    actorSystem.scheduler.schedule(0 seconds, CACHE_UPDATE_HEARTBEAT seconds)(models.MonitorConfig.updateConfig)
    actorSystem.scheduler.schedule(0 seconds, CACHE_UPDATE_HEARTBEAT seconds)(models.meta.Cache.updateMpsList)
    actorSystem.scheduler.schedule(0 seconds, CACHE_UPDATE_HEARTBEAT seconds)(models.meta.Cache.updateUser)
    actorSystem.scheduler.schedule(0 seconds, CACHE_UPDATE_HEARTBEAT seconds)(models.config.CustomerConfig.refreshCustomerConfig)
    actorSystem.scheduler.schedule(0 seconds, CACHE_UPDATE_HEARTBEAT seconds)(models.checks.Tasks.addCustChecksFromDefault)
    actorSystem.scheduler.schedule(0 seconds, CACHE_UPDATE_HEARTBEAT seconds)(models.clients.Tasks.updateClients)
    actorSystem.scheduler.schedule(0 seconds, CACHE_UPDATE_HEARTBEAT seconds)(models.notification.Escalation.checkEscalation)

    Logger.info(s"Setting up email scheduler with interval = $EMAIL_HEARTBEAT seconds")
    actorSystem.scheduler.schedule(20 seconds, EMAIL_HEARTBEAT seconds)(models.notification.SendMail.sendAllMails)

    Logger.info(s"Setting up checks scheduler with interval = $CHECKS_HEARTBEAT seconds")
    actorSystem.scheduler.schedule(30 seconds, CHECKS_HEARTBEAT seconds)(models.checks.Schedule.runAllChecks)



  }


}

