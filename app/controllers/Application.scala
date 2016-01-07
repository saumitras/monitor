package controllers

import models.dao.MonitorDb
import org.markdown4j.Markdown4jProcessor
import play.api._
import play.api.mvc._
import play.api.Play.current
import org.apache.commons.io.IOUtils
import models.MonitorConfig
import org.json4s._
import org.json4s.jackson.JsonMethods.{parse => jparse}
import play.twirl.api.Html


object Application extends Controller {
  implicit private val formats = DefaultFormats
  def auth = Action {
    Ok(views.html.auth(""))
  }

  def agent = Action {
    Ok(views.html.agent(""))
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

}