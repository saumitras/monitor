package models.notification

import models.dao.Messages.{EmailRecipient, EmailEvent, LCPEvent}
import play.api.libs.mailer._
import play.api.Play.current
import models.dao.MonitorDb
import  play.api.Logger

object Notification {

  def addEventNotification(eventId:Long, mps:String, titleInternal:String, titleExternal:String,
                           bodyInternal:String, bodyExternal:String, isExternalAllowed:Boolean) = {
    Logger.info(s"Adding new event notification for eventId: $eventId")
    val recipient = models.config.CustomerConfig.getEmailRecipient(mps)


    /*

  case class EmailEvent(id:Option[Long], eventId:Long, mps:String, titleInternal:String, titleExternal:String,
                        emailMandatory:String, emailInternal:String, emailExternal:String, sentCount:Int,
                        bodyInternal:String, bodyExternal:String)
     */

    val externalRecipient = if(isExternalAllowed) recipient.external else ""

    MonitorDb.insertEventEmail(
      EmailEvent(None, eventId, mps, titleInternal, titleExternal, recipient.mandatory, recipient.internal,
        externalRecipient, 0, bodyInternal, bodyExternal))

  }

}

