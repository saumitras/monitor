import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import play.api._
import models._
import play.libs.Akka
import play.api.mvc.RequestHeader
import play.api.mvc.Handler


object Global extends GlobalSettings {


  lazy val isAgent = {
    val mode = Play.current.configuration.getString("agent_mode") match {case Some(x) => x case None => ""}
    mode.toLowerCase == "1"
  }

  override def onStart(app: Application) {
    Logger.info("GBMonitor is starting")
    models.meta.Init.init(isAgent)
  }

  override def onStop(app:Application) = {
    Logger.info("GBMonitor is stopping")
    models.agents.ActorSupervisor.getSystem.shutdown()
    Akka.system().shutdown()
    Logger.info("GBMonitor stopped")

  }

  override def onRouteRequest(request: RequestHeader): Option[Handler] = {

    request.path match {
      case _ =>
        if(isAgent) {
          Some(controllers.Application.agent)
        } else {
          Play.maybeApplication.flatMap(_.routes.flatMap {
            router =>
              router.handlerFor(request)
          })
        }
    }
  }


}
