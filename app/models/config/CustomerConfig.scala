package models.config

import models.dao.Messages.EmailRecipient
import models.dao.MonitorDb
import play.api.Logger

object CustomerConfig {

  var config = Map[String, Map[String, String]]()

  updateCustomerConfig()

  def getMpsConfig(mps:String):Map[String, String] = {
    config.getOrElse(mps,Map())
  }

  def get(mps:String, key:String):String = {
    val config = getMpsConfig(mps)
    config.getOrElse(key,"")
  }

  def getEmailRecipient(mps:String):EmailRecipient = {
    val data = getMpsConfig(mps)
    EmailRecipient(data.getOrElse("emailMandatory",""), data.getOrElse("emailInternal",""), data.getOrElse("emailExternal",""))
  }

  def updateCustomerConfig() = {
    val data = MonitorDb.getCustomerConfig()
    if(data.nonEmpty) {
      val newConfig = data.map(r => Map(r.mps -> Map(
        "id" -> r.id.get.toString,
        "emailMandatory" -> r.emailMandatory,
        "emailInternal" -> r.emailInternal,
        "emailExternal" -> r.emailExternal,
        "skipEmailRules" -> r.skipEmailRules
      ))).reduce(_ ++ _)
      config = newConfig
      Logger.info("Customer Config = " + config)
    }

  }

}
