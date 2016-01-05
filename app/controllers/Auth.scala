package controllers

import models.dao.MonitorDb
import play.api.Logger
import play.api.mvc._
import play.libs.Akka
import play.twirl.api.Html
import models.dao.Messages._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import models.auth.AuthUtils

object Auth extends Controller {

  def isAuthenticated(implicit request:RequestHeader):Boolean = {
    val inputUser = request.cookies.get("username")
    val inputPassword = request.cookies.get("password")
    (inputUser, inputPassword) match {
      case (Some(u), Some(p)) =>
        val data = models.meta.Cache.getUserInfoByEmail(u.value)
        data match {
          case Some(user) =>
            if(user.active != "1") {
              Logger.info(s"User $user is created but not yet activated")
              false
            } else {
              if(user.password != p.value) {
                Logger.info(s"Login request for $user: Password mismatch ")
                false
              } else {
                Logger.info(s"Successful login for $user")
                true
              }
            }

          case _ =>
            Logger.info(s"Login request. User $inputUser doesnot exist")
            false
        }
      case _ =>
        Logger.info(s"Login request failed. Didn't receive username and password from client session.")
        false
    }
  }

  def route(html:Html) = Action { implicit request =>
    if(isAuthenticated)
      Ok(html)
    else {
      Redirect("/auth")
    }
  }


  def addNewUser(email:String, name:String, password:String) = Action {
    AuthUtils.addNewUser(email, name, password)
    Ok("0")
  }


  def activateUser(email:String) = Action {
    AuthUtils.activateUser(email)
    Ok("0")
  }


}

