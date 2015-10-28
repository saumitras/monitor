package controllers

import models.dao.MonitorDb
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

object LcpEvents extends Controller  {

  def getAllLcpEvents() = Action{
    val events:List[Map[String, String]] = MonitorDb.getAllLcpEvents().map(e =>
      Map(
        "id" -> e.id.get.toString,
        "signature" -> e.signature,
        "name" -> e.name,
        "mps" -> e.mps,
        "h2" -> e.h2,
        "load_id" -> e.loadId,
        "source" -> e.source,
        "occurred_at" -> e.occurredAt.toString,
        "owner" -> e.owner,
        "escalation_level" -> e.escalationLevel,
        "bug" -> e.bug,
        "component" -> e.component,
        "closed_at" -> e.closedAt.toString,
        "resolution" -> e.resolution,
        "kb" -> e.kb
      )
    )

    Ok(Json.toJson(events))

  }

}
