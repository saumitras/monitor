import models.dao.Messages._
import models.dao.{LcpDb, MonitorDb, Connections}
import models.solr.ZkWatcher
import play.api._
import models._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("GBMonitor has started")

    Logger.info("Config " + Config.config)
  }

}
