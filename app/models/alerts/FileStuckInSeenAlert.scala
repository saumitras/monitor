package models.alerts

import java.sql.Timestamp
import models.dao.Messages.{LCPEvent, FileStuckInSeen, Check}
import models.dao.MonitorDb
import play.api.Logger

import models.notification.Notification

object FileStuckInSeenAlert {

  var alertsFilesStuckInSeen = ""

  def generateAlert(h2:String, mps:String, check:Check, files:List[FileStuckInSeen]) = {
    Logger.info("Raising alert for check: " + check)
    val signature = models.utils.Util.md5Hash(h2 + mps + check.cid + files.map(x => x.loadId).mkString(","))
    if(MonitorDb.getOpenLcpEvents().exists(_.signature == signature)) {
      Logger.info(s"There is already an open event with signature=$signature h2=$h2 mps=$mps cid=${check.cid}")
    } else {
      Logger.info(s"Adding new open event with signature=$signature h2=$h2 mps=$mps cid=${check.cid}")
      val loadIds = files.map(f => f.loadId).take(10).mkString(",")
      val sources = files.map(f => f.node).distinct.take(10).mkString(",")
      val event = LCPEvent(None, check.id.getOrElse(0), check.cid, signature, "open", check.description, mps, h2, loadIds, sources, new Timestamp(System.currentTimeMillis), "none",
        "L3", "NA", "NA",  new Timestamp(System.currentTimeMillis), "NA", "NA")

      val newEventId = MonitorDb.insertLcpEvent(event)
      //println("New event id " + newEventId)

      val (title, body) = getEmailBody(newEventId, files, event)

      val isExternalAllowed = check.emailExternal == "1"

      Notification.addEventNotification(newEventId, mps, title, title, body, body, isExternalAllowed)

    }
  }
//case class FileStuckInSeen(mps:String, loadId:Long, node:String, ts:Timestamp, obs_ts:Timestamp,
//                           seen:Timestamp, fileType:Byte, name:String)
  def getEmailBody(eventId:Long, files:List[FileStuckInSeen], event:LCPEvent):(String,String) = {

    val title = s"E-$eventId [${event.mps}] ${event.name} [${event.escalationLevel}] "

    val MAX_LOAD_ID_TO_DISPLAY = 20
    val MAX_ROWS_TO_DISPLAY_IN_TABLE = 50

    val count = files.size
    val loadIds = files.map(f => f.loadId).distinct

    var body = s"<p>Number of files stuck in seen: <b>$count</b><br><br>" +
      "Total LoadID(s) = <b>" + loadIds.size + "</b><br><br>" +
      "LoadID(s)= " + loadIds.take(MAX_LOAD_ID_TO_DISPLAY).mkString(",")

    if(loadIds.size > MAX_LOAD_ID_TO_DISPLAY) {
      val diff = loadIds.size - MAX_LOAD_ID_TO_DISPLAY
      body += s" <b>and $diff more</b>"
    }

    body += "<br><br><h4>List of files stuck in seen stage </h4> <br>"

    val tableContent = models.utils.Util.emailColsToTableRows(
      List("LoadId", "Node", "TS", "ObsTs", "Seen", "FileType", "Name"),
      files.take(MAX_ROWS_TO_DISPLAY_IN_TABLE).map(x =>
        List(x.loadId.toString, x.node, x.ts.toString, x.obs_ts.toString, x.seen.toString,
          x.fileType.toString, x.name)
      )
    )

    body += tableContent

    (title, body)
  }
}

