package models.notification

import java.sql.Timestamp
import models.dao.MonitorDb
import play.api.Logger

object Escalation {


  def checkEscalation() = {

    val L2_DURATION = MonitorDb.getConf().getOrElse("l2_escalation_time",Constants.DEFAULT_L2_ESCALATION_TIME).toInt * 60
    val L3_DURATION = MonitorDb.getConf().getOrElse("l3_escalation_time",Constants.DEFAULT_L2_ESCALATION_TIME).toInt * 60

    check("L2", L2_DURATION)
    check("L3", L3_DURATION)

    def check(level:String, duration:Int) = {
      Logger.info("Checking escalation")
      //get all open events since last 4 hours
      //create a new email template for them and send email

      val L2Time = new Timestamp(System.currentTimeMillis() / 1000 - duration).getTime
      val openEvents = MonitorDb.getOpenLcpEvents().filter(_.escalationLevel == level)

      for(e <- openEvents) {
        val id = e.id
        val occurredTime = e.occurredAt.getTime
        //Logger.info(s"[Escalation] id=$id occurred=$occurredTime L2Time=$L2Time")
        if((occurredTime / 1000) < L2Time) {
          if(level == "L3") escalate(id.get, "L2") else escalate(id.get, "L1")
        }
      }
    }

    def escalate(id:Long, level:String) = {
      Logger.info(s"[Escalation] Escalating event id=$id to level=$level")
      MonitorDb.updateEscalationLevel(id, level)
      sendEscalationMail(id, level)
    }

    def sendEscalationMail(id:Long, level:String) = {
      val mailDetails = MonitorDb.getEventEmailById(id)
      val title = mailDetails.titleInternal
      val body = mailDetails.bodyInternal
      val recipients = mailDetails.emailInternal + "," + mailDetails.emailMandatory

      val newTitle = if(level == "L2")
        title.replace("[L3]", "[L2]")
      else
        title.replace("[L2]", "[L1]")

      val newBody = s"<p><b>Escalating this event to level $level. Please look at event and update it to avoid further escalation.</b></p>" + body

      Logger.info(s"Sending escalation email for event id = $id")
      SendMail.sendMail(newBody, "ESCALATED " + newTitle, recipients.split(",").toSeq, Seq[String]())

    }





  }

}
