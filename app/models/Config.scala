package models

import models.dao.MonitorDb
import play.api.Logger

object Config {

  var config = Map[String, String]()
  var h2Hosts = List[String]()
  var zkHosts = List[String]()

  def updateConfig() = {
    config = MonitorDb.getConf()
    h2Hosts = config.getOrElse("h2", "").split(",").filter(_.nonEmpty).toList
    zkHosts = config.getOrElse("zk", "").split("|").filter(_.nonEmpty).toList
  }
}
