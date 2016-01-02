package controllers

import models.dao.MonitorDb
import org.markdown4j.Markdown4jProcessor
import play.api._
import play.api.mvc._
import play.api.Play.current
import org.apache.commons.io.IOUtils
import models.MonitorConfig
import play.api.libs.json._
import play.twirl.api.Html


object Application extends Controller {

  def auth = Action {
    Ok(views.html.auth(""))
  }

  def index =  {
    Auth.route(views.html.index(""))
  }

  def lcpevents =  {
    Auth.route(views.html.lcpevents(""))
  }

  def solrevents =  {
    Auth.route(views.html.solrevents(""))
  }

  def clients =  {
    Auth.route(views.html.clients(""))
  }

  def lcpchecks =  {
    Auth.route(views.html.lcpchecks(""))
  }

  def solrchecks =  {
    Auth.route(views.html.solrchecks(""))
  }

  def reports =  {
    Auth.route(views.html.reports(""))
  }

  def admin =  {
    Auth.route(views.html.admin(""))
  }

  def kb =  {
    Auth.route(views.html.kb(""))
  }

  def custconfig =  {
    Auth.route(views.html.custconfig(""))
  }


  def emailTest = Action {
    models.notification.SendMail.sendMailOld()
    Ok("Sent")
  }

  def parsefeedback() = Action {request =>
    val paramVal = request.body.asFormUrlEncoded.get("feedback")(0)
    //val x = Json.toJson(paramVal)
    //println(x)
    println(paramVal)
    Ok("9")
  }

}