package models.notification

import models.dao.Messages.{EmailEvent, LCPEvent}
import play.api.libs.mailer._
import play.api.Play.current
import models.dao.MonitorDb

object Notification {

  def addEventNotification(eventId:Long, mps:String, recipients:String, title:String, content:String) = {
    println(s"Adding new event notification for eventId: $eventId")
    MonitorDb.insertEventEmail(EmailEvent(None, eventId, mps, title,recipients, 0, content))
  }

}

