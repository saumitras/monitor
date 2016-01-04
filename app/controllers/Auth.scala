package controllers

import models.dao.MonitorDb
import play.api.mvc._
import play.libs.Akka
import play.twirl.api.Html
import models.dao.Messages._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

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

  def addNewUser(email:String, name:String, password:String) = Action {
    val existingUsers = MonitorDb.getUser().find(_.email == email).toList
    if(existingUsers.isEmpty) {
      val user = User(email, name, password, "admin", "0", "0")
      println("Inserting user " + email)
      MonitorDb.insertUser(user)
      models.meta.Cache.updateUser()
      Akka.system.scheduler.scheduleOnce(0 seconds)(sendNewUserRegistrationMail(user))
      Ok("0")
    } else {
      Ok("1")
    }

  }

  def sendNewUserRegistrationMail(u:User) = {
    val body = s"Dear ${u.name},<br><br>" +
      s"Greetings!!" +
      s"<br><br>" +
      s"Your account is created for GBMonitor. To activate your account, please confirm your identity by replying to this mail with an empty response." +
      s"<br><br>" +
      s"You can reach us at <a href='mailto:gbmonitor@glassbeam.com'>gbmonitor@glassbeam.com</a> for any assistance." +
      s"<br><br>" +
      s"Best Regards,<br>" +
      s"Monitoring Team"

    val title = "Activate your GBMonitor Account"
    val recipient = u.email
    models.notification.SendMail.sendMail(body, title, Seq(recipient))

  }

  def route(html:Html) = Action { implicit request =>
    if(isAuthenticated)
      Ok(html)
    else {
      Redirect("/auth")
    }
  }

}

