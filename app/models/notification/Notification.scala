package models.notification

import models.dao.Messages.LCPEvent
import play.api.libs.mailer._
import play.api.Play.current

object Notification {

  def sendNotification(recipients:Seq[String], title:String, content:String) = {

    sendMail(recipients, title, content)
  }

  def sendMail(recipients:Seq[String], title:String, body:String):String = {
    println("Sending email to " + recipients)
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
      "Simple email",
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

