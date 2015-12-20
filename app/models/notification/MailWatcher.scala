package models.notification

import javax.mail.Flags.Flag
import javax.mail.search.FlagTerm
import javax.mail.{Multipart, Flags, Folder, Session}
import play.api.Logger

import akka.actor.Actor

case class InitMailWatcher(host:String, user:String, password:String)
case object ReadMailBox
case class ProcessCmd(source:String, cmd:List[String])

class MailWatcher extends Actor {

  var inbox:Folder = null
  var mailBoxInitialized = false

  def receive = {
    case InitMailWatcher(host:String, user:String, password:String) =>
      val props = System.getProperties
      props.setProperty("mail.store.protocol", "imaps")
      val session = Session.getDefaultInstance(props, null)
      val store = session.getStore("imaps")

      try {
        store.connect(host, user, password)
        val inbox:Folder = store.getFolder("Inbox")
        inbox.open(Folder.READ_WRITE)
        mailBoxInitialized = true
      } catch {
        case ex:Exception =>
          Logger.error("Exception")
      }

    case ReadMailBox =>
      readMailBox()

    case ProcessCmd(source:String, cmd:List[String]) =>
      Logger.info(s"Inside  ProcessCmd. Source: $source. Commands: $cmd")
  }


  def readMailBox() = {
    if(mailBoxInitialized) {
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
            self ! ProcessCmd(subject, cmd)
          }
        }
        message.setFlag(Flag.SEEN, false)
      }
    }
  }
}


