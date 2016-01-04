package models.notification

import models.dao.MonitorDb
import play.api.libs.mailer.MailerPlugin
import play.api.libs.mailer._
import play.api.Play.current
import play.api.Logger
import java.io.File

object SendMail {

  def sendAllMails() = {
    sendUnsentEventMail()
  }

  def sendUnsentEventMail() = {
    //Logger.info("Inside sendUnsentMail")
    val unsentEmails = MonitorDb.getUnsentEventEmail()
    for(mail <- unsentEmails) {
      val bodyInternal = mail.bodyInternal
      val bodyExternal = mail.bodyExternal
      val titleInternal = mail.titleInternal
      val titleExternal = mail.titleExternal
      val emailMandatory = mail.emailMandatory.split(",").toSeq
      val emailInternal = mail.emailInternal.split(",").toSeq
      val emailExternal = mail.emailExternal.split(",").toSeq

      try {
        sendMail(bodyInternal, titleInternal, emailInternal, emailMandatory)
        sendMail(bodyExternal, titleExternal, emailExternal, emailMandatory)
        MonitorDb.updateMailSentCount("EVENT",mail.id.get)
      } catch {
        case ex:Exception =>
          Logger.error("Exception while sending email. " + ex.getMessage)
      }
    }
  }

  def sendMail(body:String, title:String, recipientsTo:Seq[String], recipientCC:Seq[String] = Seq()) = {
    println("Sending mail")
    val email = Email(
      subject = title,
      from = "Glassbeam Monitor <gbmonitor1@gmail.com>",
      to = recipientsTo.filter(_.size != 0),
      cc = recipientCC.filter(_.size != 0),
      bodyHtml = Some(body)
    )
    MailerPlugin.send(email)
    println("Sent")
  }

  def sendFeebackMail(body:String, title:String, imgPath:String) = {
    println("Sending feedback email...")
    val email = Email(
      subject = title,
      attachments = Seq(AttachmentFile("screenshot.png", new File(imgPath))),
      from = "Glassbeam Monitor <gbmonitor1@gmail.com>",
      to = Constants.FEEDBACK_RECIPIENT,
      bodyHtml = Some(body)
    )
    MailerPlugin.send(email)
    println("Sent")
  }


  def sendMailOld() = {

    val email = Email(
      "Simple custconfig",
      "saumitra.srivastav7@gmail.com",
      Seq("saumitra.srivastav7@gmail.com","saumitra.srivastav@glassbeam.com"),
      // adds attachment
      bodyText = Some("A text message"),
      bodyHtml = Some("<html>" +
        "<head>" +
        "<style> body { background-color: #CCC; } </style>" +
        "</head>" +
        "<body>" +
        "<table style='border: 2px solid red; border-collapse:collapse;  background-color: #CCC;' >" +
        "<tr><td>C1</td><td>C1</td></tr>" +
        "<tr><td>C1</td><td>C1</td></tr>" +
        "</table>" +
        "</body>" +
        "</html>")
    )
    MailerPlugin.send(email)
  }



}
