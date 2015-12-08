package models.checks


import models.dao.Messages.LCPEvent
import play.api.libs.mailer._
import play.api.Play.current

object Notification {

  def sendNotification(event:LCPEvent, category: String = "New " ) = {
    val recipients = "saumitra.srivastav7@gmail.com"
    val (title, content) = lcpEventToEmail(event, category)
    sendMail(recipients, title, content)
  }

  def lcpEventToEmail(event:LCPEvent, category:String):(String, String) = {
    /**
     * LCPEvent(id:Option[Long], signature:String, status:String, name:String, mps:String, h2:String,
     * loadId:String, source:String,  occurredAt:Timestamp, owner:String, escalationLevel:String,
     * bug:String, component:String, closedAt:Timestamp, resolution:String, kb:String)
     */
    val title = s"$category [${event.escalationLevel}] [${event.mps}] ${event.name}"
    val content = "LoadIds " + event.loadId

    (title, content)
  }

  def sendMail(recipients:String, title:String, body:String):String = {
    println("Sending email to " + recipients)
    println("Title:\n" + title + "\nBody\n" + body)
    //return ""
    val email = Email(
      title,
      "gbmonitor1@gmail.com",
      //Seq(recipients),
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
