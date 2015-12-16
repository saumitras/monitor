package models.checks.lcp

import models.MonitorConfig
import models.dao.Messages._
import models.dao.{LcpDb, MonitorDb}
import play.api.Logger

object FileChecks {

  def checkFilesStuckInSeen(cid:String): Unit = {
    //get all H2
    //get all customer checks
    val custChecks = MonitorDb.getCustomerLcpChecks("")
    for(h2 <- MonitorConfig.h2Hosts) {
      Logger.info("Checking files stuck in seen for H2: " + h2)
      val lcpDao = LcpDb.get(h2)
      for(mps <- lcpDao.getMps()) {
        //Logger.info("Mps= " + mps)
        val checks = custChecks.filter(_.mps == mps)
          .filter(_.cid == cid)
          .filter(_.status == "enabled")

        if(checks.nonEmpty) {
          val check = checks.head
          //Logger.info("check= " + check)

          val warningThreshold = check.warning_threshold
          val criticalThreshold = check.critical_threshold
          val interval = check.interval.toLong

          val lastRunTs = models.meta.Cache.getLastRunInfo(cid,mps)
          val nowTs = System.currentTimeMillis / 1000

          if(nowTs - lastRunTs > interval) {
            //Logger.info(s"Proceeding with check. lastRun = $lastRunTs and interval = $interval")
            models.meta.Cache.setLastRunInfo(cid,mps)
            val matchingFiles:List[FileStuckInSeen] = lcpDao.getFilesStuckInSeen(mps, criticalThreshold.toLong)
            if(matchingFiles.nonEmpty) {
              models.alerts.FileStuckInSeenAlert.generateAlert(h2, mps, check, matchingFiles)
            }
          } else {
            //Logger.info("Not proceeding with check.")
          }

        }
      }
    }
  }


  def checkFilesStuckInParse(cid:String): Unit = {
    //get all H2
    //get all customer checks
    val custChecks = MonitorDb.getCustomerLcpChecks("")
    for(h2 <- MonitorConfig.h2Hosts) {
      Logger.info("Checking files stuck in parsing for H2: " + h2)
      val lcpDao = LcpDb.get(h2)
      for(mps <- lcpDao.getMps()) {
        //Logger.info("Mps= " + mps)
        val checks = custChecks.filter(_.mps == mps)
          .filter(_.cid == cid)
          .filter(_.status == "enabled")

        if(checks.nonEmpty) {
          val check = checks.head
          //Logger.info("check= " + check)

          val warningThreshold = check.warning_threshold
          val criticalThreshold = check.critical_threshold
          val interval = check.interval.toLong

          val lastRunTs = models.meta.Cache.getLastRunInfo(cid,mps)
          val nowTs = System.currentTimeMillis / 1000

          if(nowTs - lastRunTs > interval) {
            //println(s"Proceeding with check. lastRun = $lastRunTs and interval = $interval")
            models.meta.Cache.setLastRunInfo(cid,mps)
            val matchingFiles:List[FileStuckInParse] = lcpDao.getFilesStuckInParsing(mps, criticalThreshold.toLong)
            if(matchingFiles.nonEmpty) {
              models.alerts.FileStuckInParseAlert.generateAlert(h2, mps, check, matchingFiles)
            }
          } else {
            //println("Not proceeding with check.")
          }

        }
      }
    }
  }






}
