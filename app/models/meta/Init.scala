package models.meta

import models.Config
import models.dao.MonitorDb
import play.api.Logger

import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Play.current
import scala.concurrent.duration._

object Init {

  def init() = {

    Logger.info("Initializing monitor db...")
    MonitorDb.createTables()
    MonitorDb.initTables()

    val INTERVAL = 20
    Logger.info(s"Setting up checks scheduler with interval = $INTERVAL seconds")
    Akka.system.scheduler.schedule(0 seconds, INTERVAL seconds)(models.checks.Schedule.runAllChecks)


  }
}
