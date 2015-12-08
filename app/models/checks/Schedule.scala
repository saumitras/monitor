package models.checks

import models.dao.{Connections, MonitorDb, LcpDb}
import models.dao.Messages._
import play.api.Logger
import models.Config

object Schedule {

  val CONFIG_DELIMITER = ",,"
  //frequency of a check has to be same across all  MPS and all H2
  //for each H2, get all MPS
  //for each MPS, check if that check is enabled
  //get the warning and critical threshold
  //What needs to be cached? Nothing..

  val CHECK_ID = Map(
    "FILE_STUCK_IN_SEEN" -> "lcp-c01"
  )

  //lcp-c01
  def checkFilesStuckInSeen(): Unit = {
    //get all H2
    //get all customer checks
    val custChecks = MonitorDb.getCustomerLcpChecks()
    for(h2 <- Config.h2Hosts) {
      Logger.info("Checking files stuck in seen for H2: " + h2)
      val lcpDao = LcpDb.get(h2)
      for(mps <- lcpDao.getMps()) {
        Logger.info("Mps= " + mps)
        val checks = custChecks.filter(_.mps == mps)
                    .filter(_.cid == CHECK_ID("FILE_STUCK_IN_SEEN"))
                    .filter(_.status == "enabled")

        if(checks.nonEmpty) {
          val check = checks.head
          Logger.info("check= " + check)

          val warningThreshold = check.warning_threshold
          val criticalThredhold = check.critical_threshold

          val matchingFiles:List[FileStuckInSeen] = lcpDao.getFilesStuckInSeen(mps, criticalThredhold.toLong)
          if(matchingFiles.nonEmpty) {
            models.alerts.FileStuckInSeenAlert.generateAlert(h2, mps, check, matchingFiles)
          }
        }
      }
    }
  }

  def updateClients() = {
    val config = MonitorDb.getConf()
    val clients = MonitorDb.getClients()

    val groups = List("h2","zk", "lcp")

    groups.foreach(addClient)
    groups.foreach(deleteClient)

    def addClient(group:String) = {
      val clientInConfig = config.getOrElse(group,"").split(CONFIG_DELIMITER).filter(_.nonEmpty).toList
      val clientInClientsTable = clients.filter(_.group == group).map(r => r.name).toList

      val newClients = clientInConfig diff clientInClientsTable
      newClients.foreach(insertInClient)

      def insertInClient(client:String) = {
        val row = Client(client, group, "enabled", "healthy", "none", 0L)
        Logger.info("Adding to client table: " + client)
        MonitorDb.insertClient(row)
      }
    }

    //check if anything is removed from the config, then delete all those checks as well
    def deleteClient(group:String) = {
      //check if there is any client which is missing from config but present in clientsTable - delete such client from ClientT
      val clientInConfig = config.getOrElse(group,"").split(CONFIG_DELIMITER).filter(_.nonEmpty).toList
      val clientInClientsTable = clients.filter(_.group == group).map(r => r.name).toList

      val deletedClients = clientInClientsTable diff clientInConfig
      deletedClients.foreach(deleteFromClient)

      def deleteFromClient(name:String) = {
        Logger.info(s"Deleting $name from client table")
        MonitorDb.deleteFromClient(name)
      }
    }



  }


}
