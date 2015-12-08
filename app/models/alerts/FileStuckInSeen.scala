package models.alerts

import java.sql.Timestamp
import models.dao.Messages.{LCPEvent, FileStuckInSeen, Check}
import models.dao.MonitorDb
import play.api.Logger

import models.checks.Notification

object FileStuckInSeenAlert {

  var alertsFilesStuckInSeen = ""

  def generateAlert(h2:String, mps:String, check:Check, files:List[FileStuckInSeen]) = {
    Logger.info("Raising alert for check: " + check)
    val signature = MD5.hash(h2 + mps + check.cid + files.map(x => x.loadId).mkString(","))
    if(MonitorDb.getOpenLcpEvents().exists(_.signature == signature)) {
      Logger.info(s"There is already an open event with signature=$signature h2=$h2 mps=$mps cid=${check.cid}")
    } else {
      Logger.info(s"Adding new open event with signature=$signature h2=$h2 mps=$mps cid=${check.cid}")
      val loadIds = files.map(f => f.loadId).take(10).mkString(",")
      val sources = files.map(f => f.node).distinct.take(10).mkString(",")
      val event = LCPEvent(None, signature, "open", check.description, mps, h2, loadIds, sources, new Timestamp(System.currentTimeMillis), "none",
        "L3", "NA", "NA",  new Timestamp(System.currentTimeMillis), "NA", "NA")

      val newEventId = MonitorDb.insertLcpEvent(event)
      println("New event id " + newEventId)

      val (title, body) = getEmailBody(files, event)
      val recipient = models.config.CustomerConfig.get(mps,"internalEmailRecipients")
      //Notification.sendMail(recipient,title,body)
      //Notification.sendNotification(event)
    }
  }
//case class FileStuckInSeen(mps:String, loadId:Long, node:String, ts:Timestamp, obs_ts:Timestamp,
//                           seen:Timestamp, fileType:Byte, name:String)
  def getEmailBody(files:List[FileStuckInSeen], event:LCPEvent):(String,String) = {

    val title = s"[${event.mps}] ${event.name} [${event.escalationLevel}] "

    val MAX_LOAD_ID_TO_DISPLAY = 20
    val borderStyle = " style='border: 1px solid #e9e9e9;'"
    println("sending email...")

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

    body += "<table>" +
      "<tr>" +
      //s"<th $borderStyle>MPS</th>" +
      s"<th $borderStyle>LoadId</th>" +
      s"<th $borderStyle>Node</th>" +
      s"<th $borderStyle>TS</th>" +
      s"<th $borderStyle>ObsTs</th>" +
      s"<th $borderStyle>Seen</th>" +
      s"<th $borderStyle>FileType</th>" +
      s"<th $borderStyle>Name</th></tr>";

    for(f <- files.take(50)) {
      body += "<tr>" +
        //s"<td $borderStyle>" + f.mps + "</td>" +
        s"<td $borderStyle>" + f.loadId + "</td>" +
        s"<td $borderStyle>" + f.node + "</td>" +
        s"<td $borderStyle>" + f.ts + "</td>" +
        s"<td $borderStyle>" + f.obs_ts + "</td>" +
        s"<td $borderStyle>" + f.seen + "</td>" +
        s"<td $borderStyle>" + f.fileType + "</td>" +
        s"<td $borderStyle>" + f.name + "</td>" +
        "</tr>"
    }

    body += "</table>"

    //println(body)
    (title, body)
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

