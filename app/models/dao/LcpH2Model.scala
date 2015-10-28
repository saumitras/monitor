package models.dao

import java.sql.Timestamp
import java.text.SimpleDateFormat
import models.dao.Messages._
import models.lcp.ProcessingState
import org.joda.time.DateTime
import play.Play

import scala.slick.driver.H2Driver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession
import scala.slick.driver.H2Driver.backend.DatabaseDef
import scala.slick.util.TupleMethods._

object LcpDb {
  var ref = Map[String, LcpH2Model]()
  def get(h2Url:String) = {
    if(! ref.contains(h2Url)) {
      val conn = Connections.getH2(h2Url)
      val dao = new LcpH2Model(conn)
      ref += (h2Url -> dao)
    }
    ref(h2Url)
  }
}

class LcpH2Model(dbConn:DatabaseDef) {
  private case class LcpSS(runid: Option[Long], node: String, start_ts: Timestamp, heartbeat_ts: Timestamp)

  private class LcpStartStop(tag: Tag) extends Table[LcpSS](tag, "LCP_START_STOP") {
    def runid = column[Option[Long]]("RUN_ID", O.PrimaryKey, O.AutoInc)
    def node = column[String]("NODE", O.DBType("VARCHAR(15)"))
    def start_ts = column[Timestamp]("START_TS")
    def heartbeat_ts = column[Timestamp]("HEARTBEAT_TS")

    def * = (runid, node, start_ts, heartbeat_ts) <> (LcpSS.tupled, LcpSS.unapply)
  }


  private class OpsTable(tag: Tag) extends Table[(Timestamp, Timestamp, Long, Timestamp, Option[Timestamp], Option[Timestamp],
    Byte, Option[Byte], Long, Option[Byte], String, String, String, Option[String], Option[String], String, Option[String],
    Option[String], Int, Option[Long], Option[String])](tag, "OPS") {

    def ts      = column[Timestamp]("TS")
    def obsts   = column[Timestamp]("OBSTS")
    def load_id = column[Long]("LOAD_ID")
    def seen    = column[Timestamp]("SEEN")
    def started = column[Option[Timestamp]]("STARTED")
    def completed = column[Option[Timestamp]]("COMPLETED")
    def file_type = column[Byte]("FILE_TYPE")
    def processing_state = column[Option[Byte]]("PROCESSING_STATE")
    def size = column[Long]("SIZE")
    def logvaultstatus = column[Option[Byte]]("LOGVAULT_STATUS")
    def node = column[String]("NODE", O.DBType("VARCHAR(15)"))
    def name = column[String]("NAME", O.DBType("VARCHAR(4096)"))
    def spl = column[String]("SPL", O.DBType("VARCHAR(200)"))
    def parser = column[Option[String]]("PARSER", O.DBType("VARCHAR(200)"))
    def context = column[Option[String]]("CONTEXT")
    def ec = column[String]("CUSTOMER", O.DBType("VARCHAR(200)"))
    def system = column[Option[String]]("SYSTEM", O.DBType("VARCHAR(200)"))
    def notes = column[Option[String]]("NOTES", O.DBType("VARCHAR(4096)"))
    def error_count = column[Int]("ERROR_COUNT")
    def linecount = column[Option[Long]]("LINECOUNT")
    def mime = column[Option[String]]("MIME", O.DBType("VARCHAR(1024)"))

    def * = ts ~ obsts ~ load_id ~ seen ~ started ~ completed ~ file_type ~ processing_state ~ size ~ logvaultstatus ~ node ~
      name ~ spl ~ parser ~ context ~ ec ~ system ~ notes ~ error_count ~ linecount ~ mime

  }


  private case class LoadId(id: Option[Long], node: String, mps: String, bundleType: Byte, bundleName: String,
                            rxSize: Long, pxSize: Option[Long], skipSize: Option[Long],
                            rxCount: Option[Long], pxCount: Option[Long], skipCount: Option[Long],
                            properties: Option[String], completeInOps: Boolean, parseComplete: Boolean,
                            seenTime: Timestamp, completeInOpsTime: Option[Timestamp], parseCompleteTime: Option[Timestamp],
                            bundleState: Byte)

  private class LoadIds(tag: Tag) extends Table[LoadId](tag, "LOAD_ID") {

    def load_id = column[Option[Long]]("LOAD_ID", O.PrimaryKey, O.AutoInc)
    def node = column[String]("NODE", O.DBType("VARCHAR(15)"))
    def mps = column[String]("MPS", O.DBType("VARCHAR(100)"))
    def bundleType = column[Byte]("TYPE")
    def bundleName = column[String]("BUNDLE_NAME", O.DBType("VARCHAR(4096)"))
    def rxSize = column[Long]("RX_SIZE") // files to {parse + skip + delete} size
    def pxSize = column[Option[Long]]("PX_SIZE") // files to parse size
    def skipSize = column[Option[Long]]("SKIP_SIZE") // files to skip size
    def rxCount = column[Option[Long]]("RX_COUNT") // files to {parse + skip + delete} count
    def skipCount = column[Option[Long]]("SKIP_COUNT") // files to skip count
    def pxCount = column[Option[Long]]("PX_COUNT") // files to parse count
    def properties = column[Option[String]]("PROPERTIES", O.DBType("VARCHAR(1024)"))
    def completeInOps = column[Boolean]("COMPLETE_IN_OPS")
    def parseComplete = column[Boolean]("PARSE_COMPLETE")
    def seenTime = column[Timestamp]("SEEN_TIME")
    def completeInOpsTime = column[Option[Timestamp]]("COMPLETE_IN_OPS_TIME")
    def parseCompleteTime = column[Option[Timestamp]]("PARSE_COMPLETE_TIME")
    def bundleState = column[Byte]("BUNDLE_STATE")

    def * = (load_id, node, mps, bundleType, bundleName, rxSize, pxSize, skipSize, rxCount, pxCount, skipCount, properties,
      completeInOps, parseComplete, seenTime, completeInOpsTime, parseCompleteTime, bundleState) <> (LoadId.tupled, LoadId.unapply)

  }

  private class ContextTable(tag: Tag) extends Table[(String, String)](tag, "CONTEXT") {
    def key = column[String]("KEY", O.PrimaryKey)
    def value = column[String]("VALUE")
    def * = key ~ value
    def idx1 = index("context_key", key)
  }

  private val LcpStartStop = TableQuery[LcpStartStop]
  private val OpsTable = TableQuery[OpsTable]
  private val LoadIds = TableQuery[LoadIds]
  private val ContextTable = TableQuery[ContextTable]

  def getMps(): List[String] = dbConn withDynSession {
    ContextTable.map(c => c.key).run.filterNot(key => key.trim.equals("loader")).toList
  }

  def getFilesStuckInSeen(mps:String, diff: Long):List[FileStuckInSeen] = dbConn withDynSession { //diff is time in seconds
    val tNow = new Timestamp(System.currentTimeMillis())
    val files = for {
      f <- OpsTable
        .filter(_.spl === mps)
        .filter(o => o.processing_state === ProcessingState.Seen.id.toByte)
        .map(r => (r.spl, r.load_id, r.node, r.ts, r.obsts, r.seen, r.file_type, r.name)).run
      if (tNow.getTime - f._6.getTime) > diff * 1000
    } yield  FileStuckInSeen.tupled(f)
    files.toList
  }

  def getFilesStuckInParsing(mps:String, diff:Long):List[FileStuckInParse] = dbConn withDynSession {
    val tNow = System.currentTimeMillis()
    val flist = for {
      f <- OpsTable
        .filter(_.spl === mps)
        .filter(o => o.processing_state === ProcessingState.Parsing.id.toByte)
        .map(r => (r.spl, r.load_id, r.node, r.ts, r.obsts, r.seen, r.started.get, r.parser, r.file_type, r.name)).run
      if (tNow - f._7.getTime) > diff * 1000
    } yield FileStuckInParse.tupled(f)
    flist.toList
  }


}
