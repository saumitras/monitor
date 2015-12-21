import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import play.api._
import models._


object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("GBMonitor is starting")

    Logger.info("Config " + MonitorConfig.config)

    models.meta.Init.init()

  }

  override def onStop(app:Application) = {
    Logger.info("GBMonitor is stopping")

    //models.agents.ActorSupervisor.getSystem.awaitTermination()
    models.agents.ActorSupervisor.getSystem.shutdown()
    Logger.info("GBMonitor stopped")

  }


}
