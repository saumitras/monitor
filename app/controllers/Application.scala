package controllers

import models.dao.MonitorDb
import org.markdown4j.Markdown4jProcessor
import play.api._
import play.api.mvc._
import play.api.Play.current
import org.apache.commons.io.IOUtils
import models.Config

object Application extends Controller {

  def index = Action {
    //val readme = scala.io.Source.fromFile(Play.application.path + "/README.md").mkString
    //val html = new Markdown4jProcessor().process(readme)

    //sendMail()
    //Ok(IOUtils.toString(Play.application.resourceAsStream("routes").get))


    Ok(views.html.index("Your new application is ready."))

  }

  def lcpevents = Action {
    Ok(views.html.lcpevents(""))
  }

  def solrevents = Action {
    Ok(views.html.solrevents(""))
  }

  def clients = Action {
    Ok(views.html.clients(""))
  }

  def lcpchecks = Action {
    Ok(views.html.lcpchecks(""))
  }

  def solrchecks = Action {
    Ok(views.html.solrchecks(""))
  }

  def reports = Action {
    Ok(views.html.reports(""))
  }

  def admin = Action {
    Ok(views.html.admin(""))
  }

  def kb = Action {
    Ok(views.html.kb(""))
  }

  def emailTest = Action {
    models.notification.SendMail.sendMailOld()
    Ok("Sent")
  }

  /*
  def sendMail() = {
    import play.api.libs.mailer._

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
*/
}