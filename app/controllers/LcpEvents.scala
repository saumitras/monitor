package controllers

import models.dao.MonitorDb
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

object LcpEvents extends Controller  {

  def getAllLcpEvents() = Action{
    val events:List[Map[String, String]] = MonitorDb.getAllLcpEvents().map(e =>
      Map(
        "id" -> e.id.get.toString,
        "status" -> e.status,
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

  def closeLcpEvent(id:Long,kb:String, closed_at:String, bug:String,  component:String, owner:String) = Action {
    println(s"Id=$id, kb:$kb, closed_at:$closed_at, bug:$bug")
    MonitorDb.closeLcpEvent(id,kb,closed_at,bug,  component, owner)
    Ok(id.toString)
  }

  def setOwner(eventId:Long, owner:String) = Action {
    println(s"Changing lcp-event owner. eventId=$eventId new-owner:$owner")
    MonitorDb.setLcpEventOwner(eventId,owner)
    Ok(eventId.toString)
  }

}
