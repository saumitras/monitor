package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json.Json

object LcpChecks extends Controller {

  def getChecksInfo(mode:String, idList:Option[String]) = Action {

    def getDefault():List[Map[String, String]] = {
      val defaultChecks = models.checks.Tasks.getDefaultLcpChecks(idList.getOrElse(""))
      val data = defaultChecks.map(c =>
        Map(
          "cid" -> c.cid,
          "mps" -> c.mps,
          "desc" -> c.description ,
          "interval" -> c.interval,
          "critical_threshold" -> c.critical_threshold,
          "warning_threshold" -> c.warning_threshold,
          "threshold_unit" -> c.threshold_unit,
          "wait_duration" -> c.wait_duration,
          "status" -> c.status
        )
      )
      data
    }

    def getCust():List[Map[String, String]] = {
      val custChecks = models.checks.Tasks.getCustLcpChecks(idList.getOrElse(""))

      val data = custChecks.map(c =>
        Map(
          "id" -> c.id.get.toString,
          "cid" -> c.cid,
          "mps" -> c.mps,
          "desc" -> c.description ,
          "interval" -> c.interval,
          "critical_threshold" -> c.critical_threshold,
          "warning_threshold" -> c.warning_threshold,
          "threshold_unit" -> c.threshold_unit,
          "wait_duration" -> c.wait_duration,
          "status" -> c.status
        )
      )

      data
    }


    val resp = mode.toUpperCase match {
      case "ALL" =>
        Map("default" -> getDefault(), "cust" -> getCust())
      case "CUST" =>
        Map("cust" -> getCust())
      case "DEFAULT" =>
        Map("default" -> getDefault())
      case _ =>
        Map("default" -> getDefault(), "cust" -> getCust())

    }

    Ok(Json.toJson(resp))

  }


  def updateCheck(id:String, mps:String, name:String, interval:String, criticalThreshold:String,
                  warningThreshold:String, waitDuration:String, status:String) = Action {

    val result = models.checks.Tasks.updateLcpCheck(id, mps, name, interval, criticalThreshold, warningThreshold, waitDuration, status)
    Ok(result)

  }

}
