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
import models.meta.Cache

import scala.language.postfixOps

class MailWatcher() extends Actor {

  var inbox:Folder = null
  var store:Store = null

  var mailBoxInitialized = false
  var POLLING_INTERVAL = 30 seconds

  def receive = {
    case InitMailWatcher(host:String, user:String, password:String, pollingInterval:Int) =>
      Logger.info(s"InitMailWatcher called with host=$host, user:$user, password:$password, pollingInterval:$pollingInterval")
      POLLING_INTERVAL = pollingInterval seconds
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


    case ProcessMailCmd(subject:String,  fromEmail:String, cmd:List[String]) =>
      Logger.info(s"Inside ProcessCmd. From:$fromEmail Subject: $subject. Commands: $cmd")
      if(models.meta.Cache.doesUserExists(fromEmail, false)) {
        if(subject.matches(".*Activate your GBMonitor Account.*")) {
          activateUser(fromEmail)
        }

        val eventId = subject.replaceAll(".*E-","").replaceAll(" .*","")
        if(eventId.matches("\\d{1,}")) {
          cmd.filter(_.size != 0).foreach(c => processCommands(eventId, fromEmail, c))
        }
      } else {
        Logger.warn(s"User with email '$fromEmail' doesnot exists in database.")
      }

  }


  def activateUser(email:String) = {
    println(s"Activating user $email")
    models.auth.AuthUtils.activateUser(email)
  }


  def readMailBox() = {
    if(mailBoxInitialized && store.isConnected) {
      //Logger.info("Reading mailbox")

      try {
        inbox.open(Folder.READ_WRITE)
        val messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false))
        Logger.info(s"Found ${messages.size} new messages")
        for (message <- messages) {
          val subject = message.getSubject
          val from = message.getFrom.toList.head.asInstanceOf[InternetAddress].getAddress

          val commands:List[String] = Constants.MAIL_PROVIDER.toUpperCase match {
            case "AWS" =>
              parseMultipart(message)

            case "GMAIL" => //TODO: remove this case...not needed
              val mp = message.getContent.asInstanceOf[Multipart]
              val mpCount = mp.getCount
              if(mpCount > 0) {
                val bp = mp.getBodyPart(0)
                val text = bp.getContent.toString
                val lines = text.split("\n")
                val cmd = lines.head.split(";").map(_.trim).filter(_.nonEmpty).toList
                cmd
              } else {
                List()
              }
          }
          self ! ProcessMailCmd(subject, from, commands)
          message.setFlag(Flag.SEEN, true)
        }

        inbox.close(false)

      } catch  {
        case ex:Exception =>
          Logger.error("[MailWatcher] Error while reading mailbox. " + ex.getMessage)
          println(ex.getStackTrace)
      }

    }
  }

  def parseMultipart(message: Message) = {
    val lines = message.getContent.toString.split("\n")
    val cmd = lines.head.split(";").map(_.trim).filter(_.nonEmpty).toList

    if(cmd.length != 0 ) {
      if(cmd(0).matches(".*MimeMultipart.*")) {
        val mp = message.getContent.asInstanceOf[Multipart]
        val mpCount = mp.getCount
        if(mpCount > 0) {
          val bp = mp.getBodyPart(0)
          val text = bp.getContent.toString
          val lines = text.split("\n")
          val cmd = lines.head.split(";").map(_.trim).filter(_.nonEmpty).toList
          cmd
        } else {
          List()
        }
      } else {
        cmd
      }
    } else {
      cmd

    }
  }

  def processCommands(eventId:String, fromEmail:String, cmd:String) = {

    Logger.info(s"Inside proceesCommands. cmd=$cmd, size=${cmd.size}, from=$fromEmail, eventId=$eventId")

    val closeCmdRegex = "(?i)close\\s+this\\s+with\\s+component\\s*=\\s*(.*)".r
    val makeOwnerRegex = "(?i)make\\s+(.*)\\s+owner".r

    var matchFound = false
    if(!matchFound) tryChangeOwner
    if(!matchFound) tryCloseEvent

    def tryChangeOwner = {
      //change owner command
      try {
        val groups = {
          val m = makeOwnerRegex.findAllIn(cmd)
          m.hasNext
          m.subgroups
        }

        if(groups.length == 1) {
          matchFound = true
          val owner = groups(0).trim
          if(owner.toUpperCase == "ME") {
            changeOwner(eventId, fromEmail)
          } else {
            if(models.meta.Cache.doesUserExists(owner, true)) {
              val userInfo = Cache.getUserInfoByName(owner)
              changeOwner(eventId, userInfo.email)
            } else {
              Logger.warn(s"User: $owner doesnot exists in database.")
            }
          }
        }

      } catch {
        case ex:Exception =>
          Logger.error("Exception occurred which processing email watcher command. " + ex.getMessage)
      }

    }

    def tryCloseEvent = {
      //close bug option
      try {
        val groups = {
          val m = closeCmdRegex.findAllIn(cmd)
          m.hasNext
          m.subgroups
        }

        if(groups.length == 1) {
          matchFound = true
          val component = groups(0).trim
          val validComponents = List("platform","solution","ops")

          if(validComponents.contains(component)) {
            closeEvent(eventId.toLong, component, fromEmail)
          } else {
            Logger.warn(s"MailWatcher. Requested component = $component is not a valid component")
          }
        }

      } catch {
        case ex:Exception =>
          Logger.error("Exception occurred which processing email watcher command. " + ex.getMessage)
      }

    }

  }

  def closeEvent(eventId:Long, component:String, owner:String) = {
    Logger.info(s"[MailWatcher] Closing event=$eventId component=$component owner=$owner")
    models.dao.MonitorDb.closeLcpEvent(eventId, "","","",component,owner)
  }

  def changeOwner(eventId:String, owner:String) = {
    Logger.info(s"== Changing owner eventId=$eventId owner=$owner")
    models.dao.MonitorDb.setLcpEventOwner(eventId.toLong, owner)
  }

}


