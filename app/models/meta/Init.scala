package models.meta

import models.Config
import models.dao.MonitorDb
import play.api.Logger

object Init {

  def init() = {

    Logger.info("Initializing monitor db...")
    MonitorDb.createTables()
    MonitorDb.initTables()
    Config.updateConfig()

    models.checks.Tasks.addCustChecksFromDefault()
    models.checks.Schedule.checkFilesStuckInSeen()

    models.clients.Tasks.updateClients()


  }
}
