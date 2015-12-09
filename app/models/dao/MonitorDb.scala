package models.dao


import java.sql.Timestamp

import play.Logger
import play.api.Play

import scala.slick.jdbc.JdbcBackend.Database.dynamicSession
import scala.slick.driver.H2Driver.simple._
import scala.slick.lifted.TableQuery
import scala.slick.driver.H2Driver.backend.DatabaseDef

import models.dao.Messages._
import models.Config

object MonitorDb {

  val monitorH2 = Play.current.configuration.getString("monitor_h2_host") match {
    case Some(x) => x
    case None => Constants.DEFAULT_MONITOR_DB
  }
  val dbConn:DatabaseDef = Connections.getH2(monitorH2)

  class MonitorConfig(tag:Tag) extends Table[(String, String)](tag, "MONITOR_CONFIG") {
    def key = column[String]("key", O.PrimaryKey)
    def value = column[String]("value")
    def * = (key, value)
  }
  val monitorConfig = TableQuery[MonitorConfig]

  class LcpDefaultChecksT(tag: Tag) extends Table[DefaultCheck](tag, "LCP_DEFAULT_CHECKS") {
    def cid = column[String]("cid", O.PrimaryKey)
    def mps = column[String]("mps")
    def description = column[String]("description")
    def interval = column[String]("interval")
    def critical_threshold = column[String]("critical_threshold")
    def warning_threshold = column[String]("warning_threshold")
    def threshold_unit = column[String]("threshold_unit")
    def wait_duration = column[String]("wait_duration")
    def status = column[String]("status")

    def * = (cid, mps, description, interval, critical_threshold, warning_threshold, threshold_unit, wait_duration, status) <> (DefaultCheck.tupled, DefaultCheck.unapply)
  }
  val lcpDefaultChecks = TableQuery[LcpDefaultChecksT]

  class LcpChecksT(tag: Tag) extends Table[Check](tag, "LCP_CHECKS") {
    def id = column[Option[String]]("id", O.PrimaryKey, O.AutoInc)
    def cid = column[String]("cid")
    def mps = column[String]("mps")
    def description = column[String]("description")
    def interval = column[String]("interval")
    def critical_threshold = column[String]("critical_threshold")
    def warning_threshold = column[String]("warning_threshold")
    def threshold_unit = column[String]("threshold_unit")
    def wait_duration = column[String]("wait_duration")
    def status = column[String]("status")

    def * = (id, cid, mps, description, interval, critical_threshold, warning_threshold, threshold_unit,
            wait_duration, status) <> (Check.tupled, Check.unapply)
  }
  val lcpChecks = TableQuery[LcpChecksT]

  class LcpEventT(tag:Tag) extends Table[LCPEvent](tag, "LCP_EVENT") {
    def id = column[Option[Long]]("id",O.PrimaryKey, O.AutoInc)
    def signature = column[String]("signature")
    def status = column[String]("status")  //open or close
    def name = column[String]("name")
    def mps = column[String]("mps")
    def h2 = column[String]("h2")
    def loadId = column[String]("load_id")
    def source = column[String]("source")
    def occurredAt = column[Timestamp]("occurred_at")
    def owner = column[String]("owner")
    def escalationLevel = column[String]("escalation_level")
    def bug = column[String]("bug")
    def component = column[String]("component")
    def closedAt = column[Timestamp]("closed_at")
    def resolution = column[String]("resolution")
    def kb = column[String]("kb")

    def * = (id, signature, status, name, mps, h2, loadId, source, occurredAt, owner, escalationLevel, bug, component,
              closedAt, resolution, kb)  <> (LCPEvent.tupled, LCPEvent.unapply)
  }
  val lcpEvent = TableQuery[LcpEventT]

  class ClientT(tag:Tag) extends Table[Client](tag, "CLIENTS") {
    def name = column[String]("name",O.PrimaryKey)
    def group = column[String]("group")
    def status = column[String]("status")
    def health = column[String]("health")
    def stashedMps = column[String]("stashed_mps")
    def stashDuration = column[Long]("stash_duration")

    def * = (name, group, status, health, stashedMps, stashDuration) <> (Client.tupled, Client.unapply)
  }
  val clients = TableQuery[ClientT]



  //separate Db for emails...move to to FS later
  val emailH2 = Play.current.configuration.getString("monitor_email_h2_host") match {
    case Some(x) => x
    case None => Constants.DEFAULT_EMAIL_DB
  }
  val emailDbConn:DatabaseDef = Connections.getH2(emailH2)

  class EmailT(tag:Tag) extends Table[Email](tag, "EMAIL") {
    def id = column[Option[Long]]("ID",O.PrimaryKey, O.AutoInc)
    def eventId = column[Long]("EVENT_ID")
    def title = column[String]("TITLE")
    def recipient = column[String]("RECIPIENT")
    def body = column[String]("BODY")

    def * = (id, eventId, title, recipient, body)  <> (Email.tupled, Email.unapply)
  }

  val email = TableQuery[EmailT]


  def createTables() = {
    val tables = List(lcpDefaultChecks, lcpChecks, monitorConfig, lcpEvent, clients, email)
    tables.foreach(t =>
      try {
        val tableName = t.baseTableRow.tableName
        Logger.info(s"Creating monitordb table: $tableName")

        if(tableName == "EMAIL") {
          emailDbConn withDynSession {  t.ddl.create }
        } else {
          dbConn withDynSession {  t.ddl.create }
        }

      } catch  {
        case e:Exception =>
          if (e.getMessage.contains("""org.h2.jdbc.JdbcSQLException: Table already exists""") ||
              e.getMessage.contains("""create table""")) {
            Logger.info(s"Table ${t.baseTableRow.tableName} already exists")
          } else {
            Logger.info(e.getMessage)
            e.printStackTrace
          }
      }
    )
  }

  def initTables() = {
    initLcpDefaultChecks
    initMonitorConfig

    def initLcpDefaultChecks = {
      val rows = List(
        DefaultCheck("lcp-c01", "default", "File stuck in seen", "300", "900", "300", "time", "0", "enabled")
      )
      rows.foreach(r =>
        try {
          dbConn withDynSession {
            lcpDefaultChecks.insert(r)
          }.run
        } catch {
          case ex:org.h2.jdbc.JdbcSQLException =>
            Logger.warn(ex.getMessage)
        }
      )
    }

    def initMonitorConfig = {
      val rows = List(
        ("h2",Constants.DEFAULT_LCP_DB),
        ("zk",Constants.DEFAULT_ZK_HOST)
      )
      rows.foreach(r =>
        try {
          dbConn withDynSession {
            monitorConfig.insert(r)
          }.run
        } catch {
          case ex:org.h2.jdbc.JdbcSQLException =>
            Logger.warn(ex.getMessage)
        }
      )
    }


  }


  def getClients() = dbConn withDynSession {
    clients.list
  }

  def insertClient(client:Client) = dbConn withDynSession {
    clients.insert(client)
  }

  def deleteFromClient(name:String) = dbConn withDynSession {
    clients.filter(_.name === name).delete
  }

  def getConf():Map[String, String] = dbConn withDynSession {
    monitorConfig.list.map(x => x._1 -> x._2).filter(_._2.nonEmpty).toMap
  }

  def getDefaultLcpChecks():List[DefaultCheck] = dbConn withDynSession {
    lcpDefaultChecks.list
  }

  def getCustomerLcpChecks():List[Check] = dbConn withDynSession {
    lcpChecks.list
  }

  def insertCheck(mps:String, dc:DefaultCheck) = dbConn withDynSession {
    val c = Check(None, dc.cid, mps, dc.description, dc.interval, dc.critical_threshold, dc.warning_threshold,
      dc.threshold_unit, dc.wait_duration, dc.status)
    lcpChecks.insert(c)
  }

  def getAllLcpEvents() = dbConn withDynSession {
    lcpEvent.list
  }

  def closeLcpEvent(id:Long, kb:String, closed_at:String, bug:String,  component:String, owner:String) = dbConn withDynSession {
    lcpEvent.filter(_.id === id)
      .map(r => (r.status, r.kb, r.closedAt, r.bug, r.component, r.owner))
      .update(("closed",kb,new Timestamp(System.currentTimeMillis),bug,  component, owner))
  }

  def setLcpEventOwner(id:Long, owner:String) = dbConn withDynSession {
    lcpEvent.filter(_.id === id)
      .map(r => (r.owner))
      .update(owner)
  }



  def getOpenLcpEvents() = dbConn withDynSession {
    lcpEvent.filter(_.status === "open").list
  }

  def insertLcpEvent(event:LCPEvent):Long = dbConn withDynSession  {
     //lcpEvent.insert(event)
    val id = (lcpEvent returning lcpEvent.map(_.id)) += event
    id.getOrElse(0)
    //(addresses returning addresses.map(_.id)) += (city, stateName, street1, street2, zip)

  }

  def updateClientConfig(action:String, group:String, nodes:String) = {
    val DELIMITER = ",,"
    Logger.info(s"Updating config for clients. action=$action, group=$group, nodes=$nodes")

    val existingClients = getConf().getOrElse(group.toLowerCase,"").split(DELIMITER).filter(_.nonEmpty)
    val newClientList = if(action.toUpperCase == "ADD") {
      existingClients ++ nodes.split(DELIMITER)
    } else {
      existingClients diff nodes.split(DELIMITER)
    }

    dbConn withDynSession {
      monitorConfig.filter(_.key === group.toLowerCase)
        .update((group.toLowerCase, newClientList.distinct.mkString(DELIMITER)))
    }
  }


}



object MonitorDbData {



}