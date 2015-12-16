package models.meta

import models.MonitorConfig
import models.dao.LcpDb
import play.api.Logger

object Cache {

  case class RunDetail(lastRunEpoch:Long)

  var MPS_LIST = Map[String, List[String]]()
  var LAST_RUNS_CACHE = Map[String, Map[String, Cache.RunDetail]]()

  def getMps(h2:String):List[String] = {
    MPS_LIST.getOrElse(h2, List())
  }

  def getAllMps():List[String] = {
    val data = MPS_LIST.map(x => x._2).toList.flatten.distinct
    data
  }


  def updateMpsList() = {
    for(h2 <- MonitorConfig.h2Hosts) {
      val lcpDao = LcpDb.get(h2)
      val newMps = lcpDao.getMps()
      val curMps = MPS_LIST.getOrElse(h2, List())
      if(! newMps.equals(curMps)) {
        MPS_LIST += (h2 -> newMps)
        val toBeAdded = newMps.diff(curMps)
        toBeAdded.foreach(models.config.CustomerConfig.addDefaultCustomerConfigEntry)
        models.config.CustomerConfig.refreshCustomerConfig()
      }
    }
  }
  /**
   * Return epoch of last time this check was run
   * @param checkId
   * @param mps
   * @return
   */

  def getLastRunInfo(checkId:String, mps:String):Long = {

    //println(s"Finding last run info for checkId=$checkId mps=$mps")
    //check if there is an entry in cache for given check-id
    //if not add that check-id
    LAST_RUNS_CACHE.get(checkId) match {
      case Some(c) =>
        //check if mps is there for that check-id
        //if not, add that mps and initialize it with 0 epoch
        c.get(mps) match {
          case Some(info) =>
            info.lastRunEpoch
          case None =>
            val existingData = LAST_RUNS_CACHE.getOrElse(checkId, Map[String, RunDetail]())
            val newData = existingData ++ Map(mps -> RunDetail(0))
            LAST_RUNS_CACHE += (checkId -> newData)
            getLastRunInfo(checkId, mps)
        }
      case None =>
        LAST_RUNS_CACHE += (checkId -> Map())
        getLastRunInfo(checkId, mps)
    }
  }

  def setLastRunInfo(checkId:String, mps:String) = {
    //println(s"Setting last run info for checkId=$checkId mps=$mps")
    val nowTs = System.currentTimeMillis / 1000
    val existingData = LAST_RUNS_CACHE.getOrElse(checkId, Map[String, RunDetail]())
    val newData = existingData ++ Map(mps -> RunDetail(nowTs))
    LAST_RUNS_CACHE += (checkId -> newData)
  }
}
