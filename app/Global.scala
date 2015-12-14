import play.api._
import models._


object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("GBMonitor has started")

    Logger.info("Config " + Config.config)

    models.meta.Init.init()

  }


}
