package models.auth

import models.dao.MonitorDb
import models.meta.Cache
import models.dao.Messages._
import play.libs.Akka
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Logger

object AuthUtils {

  def addNewUser(email:String, name:String, password:String) {
    val existingUsers = MonitorDb.getUser().find(_.email == email).toList
    if(existingUsers.isEmpty) {
      val user = User(email, name, password, "admin", "0", "0")
      Logger.info("Inserting user new " + email)
      MonitorDb.insertUser(user)
      Cache.updateUser()
      Akka.system.scheduler.scheduleOnce(0 seconds)(sendNewUserRegistrationMail(user))

    } else {
      println(s"Trying to add new user: $email, but it already exists")

    }
  }

  def sendNewUserRegistrationMail(u:User) = {
    val body = s"Dear ${u.name},<br><br>" +
      s"Greetings!!" +
      s"<br><br>" +
      s"Your account is created for GBMonitor. To activate your account, please confirm your identity by replying to this mail." +
      s"<br><br>" +
      s"You can reach us at <a href='mailto:gbmonitor@glassbeam.com'>gbmonitor@glassbeam.com</a> for any assistance." +
      s"<br><br>" +
      s"Best Regards,<br>" +
      s"Monitoring Team"

    val title = "Activate your GBMonitor Account"
    val recipient = u.email
    models.notification.SendMail.sendMail(body, title, Seq(recipient))

  }


  def activateUser(email:String) = {
    val user = MonitorDb.getUser(email)
    if(user.nonEmpty) {
      val isActive = user.head.active == "1"
      if(isActive) {
        println(s"User $email is already activated")
      } else {
        MonitorDb.activateUser(email)
        Cache.updateUser()
        Akka.system.scheduler.scheduleOnce(0 seconds)(sendActivationMail(user.head))
        print(s"User $email is now activated")
      }

    } else {
      println(s"Tried to active user $email, but it does not exist")
    }
  }

  def sendActivationMail(u:User) = {
    val body = s"Dear ${u.name},<br><br>" +
      s"Greetings!!" +
      s"<br><br>" +
      s"Thanks for confirming your email. You can now login and start using the application." +
      s"<br><br>" +
      s"You can reach us at <a href='mailto:gbmonitor@glassbeam.com'>gbmonitor@glassbeam.com</a> for any assistance." +
      s"<br><br>" +
      s"Best Regards,<br>" +
      s"Monitoring Team"

    val title = "Your GBMonitor Account is now active"
    val recipient = u.email
    models.notification.SendMail.sendMail(body, title, Seq(recipient))
  }


}
