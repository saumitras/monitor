package models.checks

import models.dao.Messages.LCPEvent
import play.api.libs.mailer._
import play.api.Play.current

object Notification {

  def sendNotification(event:LCPEvent) = {
    val recipients = "saumitra.srivastav7@gmail.com"
    val (title, content) = lcpEventToEmail(event)
    sendMail(recipients, title, content)
  }

  def lcpEventToEmail(event:LCPEvent):(String, String) = {

    var title = "title"
    var content = "content"

    (title, content)
  }

  def sendMail(recipients:String, title:String, body:String) = {
    println("Sending email to " + recipients)
    val email = Email(
      title,
      "gbmonitor1@gmail.com",
      Seq("saumitra.srivastav7@gmail.com","saumitra.srivastav@glassbeam.com"),
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
