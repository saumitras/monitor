package models.checks

import models.Config
import models.dao.{MonitorDb, LcpDb}
import play.api.Logger

object LcpChecks {

  def init() = {
    val h2Hosts = Config.h2Hosts
    h2Hosts.foreach(insertNewChecks)

    def insertNewChecks(h2Url:String) = {
      //get all mps
      val lcpDbDao = LcpDb.get(h2Url)
      val mpsList = lcpDbDao.getMps()
      val defaultLcpChecks = MonitorDb.getDefaultLcpChecks("")
      val customerLcpChecks = MonitorDb.getCustomerLcpChecks("")

      //get all default checks
      val dfltCheckIds = defaultLcpChecks.map(x => x.cid).toList
      Logger.info("Default checks " + defaultLcpChecks)
      for (mps: String <- mpsList) {
        //get all registered checks for a MPS
        val mpsChecks = customerLcpChecks.filter(_.mps == mps).map(x => x.cid)
        Logger.info("Mps checks= " + mpsChecks)

        //get all default checks which are not registered for this MPS
        val newChecks = dfltCheckIds.filterNot(mpsChecks.contains)
        Logger.info(s"Mps=$mps NewChecks=" + newChecks)

        newChecks.foreach(cid => MonitorDb.insertCheck(mps, defaultLcpChecks.filter(_.cid == cid).head))

      }
//      Logger.info("After addition " + MonitorDb.getCustomerLcpChecks())

    }
  }

  def getDefaultLcpChecks(idList:String) = {

    val defaultLcpChecks = MonitorDb.getDefaultLcpChecks(idList)
    defaultLcpChecks

  }

  def getCustLcpChecks(idList:String) = {
    val customerLcpChecks = MonitorDb.getCustomerLcpChecks(idList)
    customerLcpChecks
  }


  def updateLcpCheck(id:String, mps:String, name:String, interval:String, criticalThreshold:String,
                  warningThreshold:String, waitDuration:String, status:String) = {
    MonitorDb.updateLcpCheck(id, mps, name, interval, criticalThreshold, warningThreshold, waitDuration, status)
    "ok"
  }


}
