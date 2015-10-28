package models.checks

import java.sql.Timestamp
import java.util.Date

import models.dao.Messages.{LCPEvent, FileStuckInSeen, Check}
import models.dao.MonitorDb
import org.joda.time.DateTime
import play.api.Logger

object LcpAlerts {

  var alertsFilesStuckInSeen = ""

  def alertFilesStuckInSeen(h2:String, mps:String, check:Check, files:List[FileStuckInSeen]) = {
    Logger.info("Raising alert for check: " + check)
    val signature = MD5.hash(h2 + mps + check.cid + files.map(x => x.loadId).mkString(","))
    if(MonitorDb.getOpenLcpEvents().exists(_.signature == signature)) {
      Logger.info(s"There is already an open event with signature=$signature h2=$h2 mps=$mps cid=${check.cid}")
    } else {
      Logger.info(s"Adding new open event with signature=$signature h2=$h2 mps=$mps cid=${check.cid}")
       val loadIds = files.map(f => f.loadId).take(10).mkString(",")
      val sources = files.map(f => f.node).distinct.take(10).mkString(",")
      val event = LCPEvent(None, signature, "open", check.description, mps, h2, loadIds, sources,  new Timestamp(System.currentTimeMillis), "none",
                          "L3", "NA", "NA",  new Timestamp(System.currentTimeMillis), "NA", "NA")
      MonitorDb.insertLcpEvent(event)

    }


  }
}

object MD5 {
  def hash(s: String) = {
    val m = java.security.MessageDigest.getInstance("MD5")
    val b = s.getBytes("UTF-8")
    m.update(b, 0, b.length)
    new java.math.BigInteger(1, m.digest()).toString(16)
  }
}
