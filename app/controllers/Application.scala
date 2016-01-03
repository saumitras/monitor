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

  def test1 = Action {
    val json =
      """
        |{
        |    "glossary": {
        |        "title": "example glossary",
        |		"GlossDiv": {
        |            "title": "S",
        |			"GlossList": {
        |                "GlossEntry": {
        |                    "ID": "SGML",
        |					"SortAs": "SGML",
        |					"GlossTerm": "Standard Generalized Markup Language",
        |					"Acronym": "SGML",
        |					"Abbrev": "ISO 8879:1986",
        |					"GlossDef": {
        |                        "para": "A meta-markup language, used to create markup languages such as DocBook.",
        |						"GlossSeeAlso": ["GML", "XML"]
        |                    },
        |					"GlossSee": "markup"
        |                }
        |            }
        |        }
        |    }
        |}
      """.stripMargin
    val data = jparse(json)
    val title = (data).extract[Map[String,Any]]
    Ok(title.keys.mkString(","))
  }
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
    //println(paramVal)
    val data = jparse(paramVal)
    val title = data.extract[Map[String,Any]]
    println(title.keys)
    Ok("9")
  }

}