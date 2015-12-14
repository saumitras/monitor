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
    val signature = MD5.hash(h2 + mps + check.cid + files.map(x => x.loadId).mkString(","))
    if(MonitorDb.getOpenLcpEvents().exists(_.signature == signature)) {
      Logger.info(s"There is already an open event with signature=$signature h2=$h2 mps=$mps cid=${check.cid}")
    } else {
      Logger.info(s"Adding new open event with signature=$signature h2=$h2 mps=$mps cid=${check.cid}")
      val loadIds = files.map(f => f.loadId).take(10).mkString(",")
      val sources = files.map(f => f.node).distinct.take(10).mkString(",")
      val event = LCPEvent(None, check.id.getOrElse(0), check.cid, signature, "open", check.description, mps, h2, loadIds, sources, new Timestamp(System.currentTimeMillis), "none",
        "L3", "NA", "NA",  new Timestamp(System.currentTimeMillis), "NA", "NA")

      val newEventId = MonitorDb.insertLcpEvent(event)
      println("New event id " + newEventId)

      val (title, body) = getEmailBody(files, event)
      val recipient = models.config.CustomerConfig.get(mps,"internalEmailRecipients")

      Notification.addEventNotification(newEventId, mps, recipient,title,body)


    }
  }
//case class FileStuckInSeen(mps:String, loadId:Long, node:String, ts:Timestamp, obs_ts:Timestamp,
//                           seen:Timestamp, fileType:Byte, name:String)
  def getEmailBody(files:List[FileStuckInSeen], event:LCPEvent):(String,String) = {

    val title = s"[${event.mps}] ${event.name} [${event.escalationLevel}] "

    val MAX_LOAD_ID_TO_DISPLAY = 20
    val borderStyleTable =  " style='border: 1px solid #000; border-collapse:collapse;'"
    val borderStyleHeader = " style='border: 1px solid #000; background-color: #333; color:#e7e7e7; font-weight: bold;'"
    val borderStyleAlt1  =  " style='border: 1px solid #000; background-color: #FFF; color:#000;'"
    val borderStyleAlt2  =  " style='border: 1px solid #000; background-color: #e7e7e7; color:#000;'"


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

    body += s"<table $borderStyleTable>";

    body +=  "<tr>" +
      s"<th $borderStyleHeader>#</th>" +
      s"<th $borderStyleHeader>LoadId</th>" +
      s"<th $borderStyleHeader>Node</th>" +
      s"<th $borderStyleHeader>TS</th>" +
      s"<th $borderStyleHeader>ObsTs</th>" +
      s"<th $borderStyleHeader>Seen</th>" +
      s"<th $borderStyleHeader>FileType</th>" +
      s"<th $borderStyleHeader>Name</th></tr>";

    var counter = 0;
    for(f <- files.take(50)) {
      counter += 1
      val selectedStyle = if(counter % 2 == 0) borderStyleAlt1 else borderStyleAlt2
      body += "<tr>" +
        s"<td $selectedStyle>" + counter + "</td>" +
        s"<td $selectedStyle>" + f.loadId + "</td>" +
        s"<td $selectedStyle>" + f.node + "</td>" +
        s"<td $selectedStyle>" + f.ts + "</td>" +
        s"<td $selectedStyle>" + f.obs_ts + "</td>" +
        s"<td $selectedStyle>" + f.seen + "</td>" +
        s"<td $selectedStyle>" + f.fileType + "</td>" +
        s"<td $selectedStyle>" + f.name + "</td>" +
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

