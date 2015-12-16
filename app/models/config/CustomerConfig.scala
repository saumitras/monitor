package models.config

import models.MonitorConfig
import models.dao.MonitorDb
import models.dao.Messages._

import play.api.Logger

object CustomerConfig {

  var config = Map[String, Map[String, String]]()

  updateCustomerConfig()

  def getMpsConfig(mps:String):Map[String, String] = {
    config.getOrElse(mps,Map())
  }

  def get(mps:String, key:String):String = {
    val data = getMpsConfig(mps)
    data.getOrElse(key,"")
  }

  def getEmailRecipient(mps:String):EmailRecipient = {
    val data = getMpsConfig(mps)
    EmailRecipient(data.getOrElse("emailMandatory",""), data.getOrElse("emailInternal",""), data.getOrElse("emailExternal",""))
  }

  def updateCustomerConfig() = {
    val data = MonitorDb.getCustomerConfig()
    if(data.nonEmpty) {
      val newConfig = data.map(r => Map(r.mps -> Map(
        "emailMandatory" -> r.emailMandatory,
        "emailInternal" -> r.emailInternal,
        "emailExternal" -> r.emailExternal,
        "skipEmailRules" -> r.skipEmailRules
      ))).reduce(_ ++ _)
      config = newConfig
      //Logger.info("Customer Config = " + config)
    }

  }


  def addDefaultCustomerConfigEntry(mps:String) = {
    val defaultInternalEmail = MonitorConfig.defaultInternalEmail
    val defaultMandatoryEmail = MonitorConfig.defaultMandatoryEmail

    val row = CustConfig(mps, defaultMandatoryEmail, defaultInternalEmail, "", "")
    MonitorDb.insertCustomerConfig(row)
  }

}
