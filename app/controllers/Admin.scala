package controllers

import models.dao.MonitorDb
import play.api.mvc.{Action, Controller}
import play.api.libs.json.Json

object Admin extends Controller {
  def client(action:String, group:String, nodes:String) = Action {
    MonitorDb.updateClientConfig(action, group, nodes)
    val resp = Map("status" -> "0",
      "msg" -> s"Client request processed: action=$action, group:$group, nodes=$nodes")
    Ok(Json.toJson(resp))
  }
}
