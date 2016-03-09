package controllers

import models.dao.MonitorDb
import play.api.mvc.{Action, Controller}
import play.api.libs.json.Json

object Client extends Controller {
  def getAllClient() = Action {
    val clients:List[Map[String, String]] = MonitorDb.getClients().map(client =>
      Map(
        "name" -> client.name,
        "group" -> client.group,
        "status" -> client.status,
        "health" -> client.health,
        "stashed_mps" -> client.stashedMps,
        "stash_duration" -> client.stashDuration.toString
      )
    )
    Ok(Json.toJson(clients))
  }


  def getSingleClientInfo(name:String) = Action {
    val client:Map[String, String] = MonitorDb.getClients().filter(_.name == name).map(client =>
      Map(
        "name" -> client.name,
        "group" -> client.group,
        "status" -> client.status,
        "health" -> client.health,
        "stashed_mps" -> client.stashedMps,
        "stash_duration" -> client.stashDuration.toString
      )
    ).head

    Ok(Json.toJson(client))

  }
}
