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

object Init {

  def init() = {

    //val actorSystem = models.agents.ActorSupervisor.monitorActorSystem

    Logger.info("Initializing monitor db...")
    MonitorDb.createTables()
    MonitorDb.initTables()

    val CHECKS_HEARTBEAT = 10
    val EMAIL_HEARTBEAT = 10
    val CACHE_UPDATE_HEARTBEAT = 20

    Logger.info(s"Setting up monitor-db cache update scheduler with interval = $CACHE_UPDATE_HEARTBEAT seconds")
    Akka.system.scheduler.schedule(0 seconds, CACHE_UPDATE_HEARTBEAT seconds)(models.MonitorConfig.updateConfig)
    Akka.system.scheduler.schedule(0 seconds, CACHE_UPDATE_HEARTBEAT seconds)(models.meta.Cache.updateMpsList)
    Akka.system.scheduler.schedule(0 seconds, CACHE_UPDATE_HEARTBEAT seconds)(models.config.CustomerConfig.refreshCustomerConfig)
    Akka.system.scheduler.schedule(0 seconds, CACHE_UPDATE_HEARTBEAT seconds)(models.checks.Tasks.addCustChecksFromDefault)
    Akka.system.scheduler.schedule(0 seconds, CACHE_UPDATE_HEARTBEAT seconds)(models.clients.Tasks.updateClients)

    Logger.info(s"Setting up checks scheduler with interval = $CHECKS_HEARTBEAT seconds")

    Akka.system.scheduler.schedule(30 seconds, CHECKS_HEARTBEAT seconds)(models.checks.Schedule.runAllChecks)



    Logger.info(s"Setting up email scheduler with interval = $EMAIL_HEARTBEAT seconds")
    Akka.system.scheduler.schedule(20 seconds, EMAIL_HEARTBEAT seconds)(models.notification.SendMail.sendAllMails)




  }


}

