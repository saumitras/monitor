package models.checks


object Schedule {

  val CHECK_ID = Map(
    "FILE_STUCK_IN_SEEN" -> "lcp-c01",
    "FILE_STUCK_IN_PARSING" -> "lcp-c02"

  )

  def runAllChecks() = {

    println("Running maintenance jobs...")
    models.Config.updateConfig()
    models.checks.Tasks.addCustChecksFromDefault()
    models.clients.Tasks.updateClients()

    println("Running all checks...")
    models.checks.lcp.FileChecks.checkFilesStuckInSeen(CHECK_ID("FILE_STUCK_IN_SEEN"))
    models.checks.lcp.FileChecks.checkFilesStuckInParse(CHECK_ID("FILE_STUCK_IN_PARSING"))

  }


}
