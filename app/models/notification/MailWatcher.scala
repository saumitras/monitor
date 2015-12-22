package models.notification

import javax.mail.Flags.Flag
import javax.mail.event.{MessageCountEvent, MessageCountListener}
import javax.mail.internet.InternetAddress
import javax.mail.search.FlagTerm
import javax.mail._
import models.dao.Messages._
import play.api.Logger
import scala.concurrent.duration._
import akka.actor.Actor
import scala.concurrent.ExecutionContext.Implicits.global

class MailWatcher extends Actor {

  var inbox:Folder = null
  var store:Store = null

  var mailBoxInitialized = false
  val POLLING_INTERVAL = 10 seconds

  def receive = {
    case InitMailWatcher(host:String, user:String, password:String) =>
      Logger.info(s"InitMailWatcher called with host=$host, user:$user, password:$password")
      val props = System.getProperties
      props.setProperty("mail.store.protocol", "imaps")
      val session = Session.getDefaultInstance(props, null)
      store = session.getStore("imaps")

      try {
        store.connect(host, user, password)
        inbox = store.getFolder("Inbox")
        mailBoxInitialized = true
        context.system.scheduler.scheduleOnce(2 seconds, self, ReadMailBox)

      } catch {
        case ex:Exception =>
          Logger.error("Exception in MailWatcher. " + ex.getMessage)
      }

    case ReadMailBox =>
      Logger.info("Got ReadMailBox message.")
      readMailBox()
      context.system.scheduler.scheduleOnce(POLLING_INTERVAL, self, ReadMailBox)


    case ProcessMailCmd(source:String, from:String, cmd:List[String]) =>
      //Logger.info(s"Inside ProcessCmd. Source: $source. Commands: $cmd")
      val eventId = source.replaceAll(".*E-","").replaceAll(" .*","")
      if(eventId.matches("\\d{1,}")) {
        cmd.filter(_.size != 0).foreach(c => processCommands(eventId, from, c))
      }
  }


  def readMailBox() = {
    if(mailBoxInitialized && store.isConnected) {
      Logger.info("Reading mailbox")

      inbox.open(Folder.READ_WRITE)
      val messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false))
      Logger.info(s"Found ${messages.size} new messages")
      for (message <- messages) {
        val subject = message.getSubject
        val from = message.getFrom.toList.head.asInstanceOf[InternetAddress].getAddress

        val mp = message.getContent.asInstanceOf[Multipart]
        val mpCount = mp.getCount

        if(mpCount > 0) {

          val bp = mp.getBodyPart(0)
          val text = bp.getContent.toString
          val lines = text.split("\n")
          if(lines.nonEmpty) {
            val cmd = lines.head.split(";").map(_.trim).filter(_.nonEmpty).toList
            self ! ProcessMailCmd(subject, from, cmd)
          }
        }
        message.setFlag(Flag.SEEN, false)
      }
      inbox.close(false)
    }
  }

  def processCommands(eventId:String, from:String, cmd:String) = {

    Logger.info(s"Inside proceesCommands. cmd=$cmd, size=${cmd.size}, from=$from, eventId=$eventId")

    val closeCmdRegex = "close this with component=(.*)".r
    val makeOwnerRegex = "make (.*) owner".r

    //change owner command
    try {
      val groups = {
        val m = makeOwnerRegex.findAllIn(cmd)
        m.hasNext
        m.subgroups
      }

      if(groups.length == 1) {
        val owner = groups(0)
        if(owner.toUpperCase == "ME") {
          changeOwner(eventId, owner)
        } else {
          changeOwner(eventId, owner)
        }
      }

    } catch {
      case ex:Exception =>
        Logger.error("Exception occurred which processing email watcher command. " + ex.getMessage)
    }




  }


  def closeEvent(eventId:Long, component:String, owner:String) = {
    models.dao.MonitorDb.closeLcpEvent(eventId, "","","",component,owner)
  }

  def changeOwner(eventId:String, owner:String) = {
    Logger.info(s"== Changing owner eventId=$eventId owner=$owner")
  }
}


