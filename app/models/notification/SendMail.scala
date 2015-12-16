package models.notification

import models.dao.MonitorDb
import play.api.libs.mailer.MailerPlugin
import play.api.libs.mailer._
import play.api.Play.current
import play.api.Logger

object SendMail {

  def sendAllMails() = {
    sendUnsentMail("event")
    sendUnsentMail("ops")
  }

  def sendUnsentMail(category:String) = {
    //Logger.info("Inside sendUnsentMail")
    val unsentEmails = MonitorDb.getUnsentEmail(category)
    for(mail <- unsentEmails) {
      //println("Sending mail. Details" + mail)
      //Logger.info(s"Sending mail. Type = $category, id=" + mail._1)
      MonitorDb.updateMailSentCount(category,mail._1)
      sendMail(mail._2.split(",").toSeq, mail._3, mail._4)
    }
  }

  def sendMail(recipients:Seq[String], title:String, body:String):String = {
    //println("Sending custconfig to " + recipients)
    //println("Title:\n" + title + "\nBody\n" + body)
    //return ""
    val email = Email(
      title,
      "Glassbeam Monitor <gbmonitor1@gmail.com>",
      recipients,
      //Seq("saumitra.srivastav7@gmail.com","saumitra.srivastav@glassbeam.com"),
      bodyHtml = Some(body)
    )
    MailerPlugin.send(email)
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
