package models.solr

object SolrStats {

  def getCoresHealth(filter:String, app:String) = {

    val solrCollections = if(app == "EXPLORER") models.solr.ZkWatcher.getExplorerCollections else models.solr.ZkWatcher.getLogvaultCollections

    var activeCores = Map[String, Map[String, String]]()
    var nonActiveCores = Map[String, Map[String, String]]()

    for((collection, stats) <- solrCollections) {
      val cores = stats.cores

      for((coreName, coreData) <- cores) {
        val baseUrl = coreData(0)
        val state = coreData(1)
        //val coreStat = CoreStat(collection, baseUrl, state)
        val coreStat = Map("collection"->collection, "baseUrl" -> baseUrl, "state" -> state)

        if(state == "active") activeCores += (coreName -> coreStat)  else nonActiveCores += (coreName -> coreStat)

      }
    }

    if(filter == "active") Map("active" -> activeCores)
    else if(filter == "inactive")  Map("inactive" -> nonActiveCores)
    else Map("active" -> activeCores, "inactive" -> nonActiveCores)

  }


}
