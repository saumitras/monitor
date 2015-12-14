package models.clients

import models.dao.Messages.Client
import models.dao.MonitorDb
import play.api.Logger

object Tasks {

  val CONFIG_DELIMITER = ",,"

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
