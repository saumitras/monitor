package models.checks

import models.dao.MonitorDb

object Schedule {

  val CHECK_ID = Map(
    "FILE_STUCK_IN_SEEN" -> "lcp-c01"
  )

  def checkFilesStuckInSeen(): Unit = {
    models.checks.lcp.FileChecks.checkFilesStuckInSeen(CHECK_ID("FILE_STUCK_IN_SEEN"))
  }


}
