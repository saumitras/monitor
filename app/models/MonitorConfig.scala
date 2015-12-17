package models

import models.dao.MonitorDb
import play.api.Logger

object MonitorConfig {

  var config = Map[String, String]()
  var h2Hosts = List[String]()
  var zkHosts = List[String]()

  def defaultMandatoryEmail = "saumitra.srivastav7@gmail.com, aklank.choudhary@glassbeam.com"
  def defaultInternalEmail = "saumitra.srivastav@glassbeam.com, aklank.choudhary@glassbeam.com"

  def updateConfig() = {
    config = MonitorDb.getConf()
    h2Hosts = config.getOrElse("h2", "").split(",").filter(_.nonEmpty).toList
    zkHosts = config.getOrElse("zk", "").split("|").filter(_.nonEmpty).toList
  }
}
