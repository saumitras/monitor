package controllers

import models.dao.MonitorDb
import models.dao.MonitorDb.LcpEventT
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import play.api.libs.json._

case class ScalarValue(x:String)
case class MapValue(x:Map[String, String])
case class Result(res:Map[String, Either[ScalarValue, MapValue]])

object Stats extends Controller {
  def getSummary() = Action {
    val data = MonitorDb.getAllLcpEvents()

    val openEvents = data.filter(x => x.status != "closed")
    val openEventsCount = openEvents.size
    val openEventsOwnerCount = openEvents.map(x => x.owner).distinct.size

    val closedEvents = data.filter(x => x.status == "closed")
    val closedEventsCount = closedEvents.size
    val closedEventsOwnerCount = closedEvents.map(x => x.owner).distinct.size

    val issueGroup = data.map(x => x.component).foldLeft(Map.empty[String, Int]) { (m, x) => m + ((x, m.getOrElse(x, 0) + 1)) }

    val bugOpenedCount = data.count(x => x.status == "closed" && x.bug.nonEmpty)

    val openEventsByMps = openEvents.map(x => x.mps).foldLeft(Map.empty[String, Int]) { (m, x) => m + ((x, m.getOrElse(x, 0) + 1)) }
    val closedEventByMps = closedEvents.map(x => x.mps).foldLeft(Map.empty[String, Int]) { (m, x) => m + ((x, m.getOrElse(x, 0) + 1)) }

    val uniqueClient = data.map(x => x.source.split(",")).flatten.distinct


    def getEventByClient(events:List[LcpEventT#TableElementType], client:List[String]) = {
      var result = Map[String, String]()
      for(c <- client) {
        var count = 0
        for(e <- events) {
          if(e.source.contains(c))
            count += 1
        }
        result += (c -> count.toString)
      }
      result
    }

    val openEventsByClient = getEventByClient(openEvents, uniqueClient)
    val closedEventsByClient = getEventByClient(closedEvents, uniqueClient)

    val response =  Json.obj(
      "openEventsCount" -> openEventsCount,
      "openEventsOwnerCount" -> openEventsOwnerCount,
      "closedEventsCount" -> closedEventsCount,
      "closedEventsOwnerCount" -> closedEventsOwnerCount,
      "issueGroup" -> issueGroup,
      "bugOpenedCount" -> bugOpenedCount,
      "openEventsByMps" -> openEventsByMps,
      "closedEventsByMps" -> closedEventByMps,
      "openEventsByClient" -> openEventsByClient,
      "closedEventsByClient" -> closedEventsByClient
    )

    println(response)

    Ok(response)

  }
}
