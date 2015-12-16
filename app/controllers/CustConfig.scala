package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json.Json

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
}
