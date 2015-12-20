package models.notification

import javax.mail.Flags.Flag
import javax.mail.search.FlagTerm
import javax.mail.{Multipart, Flags, Folder, Session}
import models.dao.Messages._
import play.api.Logger
import scala.concurrent.duration._
import akka.actor.Actor
import scala.concurrent.ExecutionContext.Implicits.global

class MailWatcher extends Actor {

  var inbox:Folder = null
  var mailBoxInitialized = false
  val POLLING_INTERVAL = 10 seconds

  def receive = {
    case InitMailWatcher(host:String, user:String, password:String) =>
      Logger.info(s"InitMailWatcher called with host=$host user:$user, password:$password")
      val props = System.getProperties
      props.setProperty("mail.store.protocol", "imaps")
      val session = Session.getDefaultInstance(props, null)
      val store = session.getStore("imaps")

      try {
        store.connect(host, user, password)
        inbox = store.getFolder("Inbox")
        inbox.open(Folder.READ_WRITE)
        mailBoxInitialized = true
        context.system.scheduler.scheduleOnce(2 seconds, self, ReadMailBox)
      } catch {
        case ex:Exception =>
          Logger.error("Exception")
      }

    case ReadMailBox =>
      Logger.info("Got ReadMailBox message.")
      readMailBox()
      context.system.scheduler.scheduleOnce(POLLING_INTERVAL, self, ReadMailBox)


    case ProcessMailCmd(source:String, cmd:List[String]) =>
      Logger.info(s"Inside ProcessCmd. Source: $source. Commands: $cmd")
  }


  def readMailBox() = {
    if(mailBoxInitialized) {
      Logger.info("Reading mailbox")
      val messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false))
      for (message <- messages) {
        val subject = message.getSubject
        val mp = message.getContent.asInstanceOf[Multipart]
        val mpCount = mp.getCount

        if(mpCount > 0) {
          val bp = mp.getBodyPart(0)
          val text = bp.getContent.toString
          val lines = text.split("\n")
          if(lines.nonEmpty) {
            val cmd = lines.head.split(";").toList
            self ! ProcessMailCmd(subject, cmd)
          }
        }
        message.setFlag(Flag.SEEN, false)
      }
    }
  }
}


