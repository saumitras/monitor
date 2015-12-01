package controllers


import play.api.mvc.{Action, Controller}
import play.api.libs.json.Json

object LcpChecks extends Controller{

  def getDefaultChecks = Action {
    //case class DefaultCheck(cid:String, mps:String, description:String, interval:String,
    // critical_threshold:String, warning_threshold:String, threshold_unit:String, wait_duration:String,
    // status:String)

    val data = models.checks.LcpChecks.getDefaultLcpChecks

    val fData = data.map(c =>
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

    Ok(Json.toJson(fData))
  }

  def getCustChecks = Action {
    val data = models.checks.LcpChecks.getCustLcpChecks
    //case class Check(id:Option[String], cid:String, mps:String, description:String, interval:String,
    // critical_threshold:String, warning_threshold:String, threshold_unit:String, wait_duration:String,
    // status:String)

    val fData = data.map(c =>
      Map(
        "id" -> c.id.get,
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

    Ok(Json.toJson(fData))

  }

}
