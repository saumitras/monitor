package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json.Json
import play.api.Logger

object CustConfig extends Controller{

  def getMpsList() = Action {
    val data = models.meta.Cache.getAllMps()
    Ok(Json.toJson(data))
  }

  def getCustomerConfig() = Action {
    val data = models.config.CustomerConfig.getAllConfig()
    Ok(Json.toJson(data))
  }

  def updateCustConfig(mps:String,emailMandatory:String, emailInternal:String, emailExternal:String, skipEmailRules:String) = Action {
    val result = models.config.CustomerConfig.updateCustConfig(mps,emailMandatory, emailInternal, emailExternal, skipEmailRules)
    models.config.CustomerConfig.refreshCustomerConfig()
    Ok("1")
  }

  def getGlobalConfig() = Action {
    val data = models.MonitorConfig.getMonitorConfig()
    Ok(Json.toJson(data))
  }

  def updateGlobalConfig() = Action { request =>

    val key = request.body.asFormUrlEncoded.get("key")(0)
    val value = request.body.asFormUrlEncoded.get("value")(0)

    models.dao.MonitorDb.updateMonitorConfig(key, value)
    Logger.info(s"Updating global config. Key=$key, value:$value")
    Ok("1")
  }


}
