package controllers

import play.api.mvc._
import play.twirl.api.Html

object Auth extends Controller {

  def isAuthenticated(implicit request:RequestHeader):Boolean = {
    val username = request.cookies.get("username")
    val password = request.cookies.get("password")
    (username, password) match {
      case (Some(u), Some(p)) =>
        val user = models.meta.Cache.getUserInfoByEmail(u.value)
        println("USer = " + user)
        user match {
          case Some(v) =>
            if(v.password == p.value) true else false
          case _ =>
            false
        }
      case _ => false
    }
  }

  def route(html:Html) = Action { implicit request =>
    if(isAuthenticated)
      Ok(html)
    else {
      Redirect("/auth")
    }
  }

}

