package models.checks.lcp

import models.Config
import models.dao.Messages.FileStuckInSeen
import models.dao.{LcpDb, MonitorDb}
import play.api.Logger

object FileChecks {

  def checkFilesStuckInSeen(cid:String): Unit = {
    //get all H2
    //get all customer checks
    val custChecks = MonitorDb.getCustomerLcpChecks("")
    for(h2 <- Config.h2Hosts) {
      Logger.info("Checking files stuck in seen for H2: " + h2)
      val lcpDao = LcpDb.get(h2)
      for(mps <- lcpDao.getMps()) {
        Logger.info("Mps= " + mps)
        val checks = custChecks.filter(_.mps == mps)
          .filter(_.cid == cid)
          .filter(_.status == "enabled")

        if(checks.nonEmpty) {
          val check = checks.head
          Logger.info("check= " + check)

          val warningThreshold = check.warning_threshold
          val criticalThredhold = check.critical_threshold

          val matchingFiles:List[FileStuckInSeen] = lcpDao.getFilesStuckInSeen(mps, criticalThredhold.toLong)
          if(matchingFiles.nonEmpty) {
            models.alerts.FileStuckInSeenAlert.generateAlert(h2, mps, check, matchingFiles)
          }
        }
      }
    }
  }

}
