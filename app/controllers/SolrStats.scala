package controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

object SolrStats extends Controller {

  def getExplorerClusterHealth(sensuFormat: Option[Boolean]) = Action {
    val stats = models.solr.SolrStats.getCoresHealth("","EXPLORER")

    if(sensuFormat.getOrElse(false)) {
      val inactive = stats.getOrElse("inactive",Map())
      if (inactive.keys.size == 0)
        Ok(Json.toJson(0))
      else
        Ok(Json.toJson(2))
    } else {
      Ok(Json.toJson(stats))
    }

  }

  def getLogvaultClusterHealth(sensuFormat: Option[Boolean]) = Action {
    val stats = models.solr.SolrStats.getCoresHealth("","LOGVAULT")

    if(sensuFormat.getOrElse(false)) {
      val inactive = stats.getOrElse("inactive",Map())
      if (inactive.keys.size == 0)
        Ok(Json.toJson(0))
      else
        Ok(Json.toJson(2))
    } else {
      Ok(Json.toJson(stats))
    }
  }


}
