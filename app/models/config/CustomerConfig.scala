package models.config

object CustomerConfig {

  def getMpsConfig(mps:String):Map[String, String] = {
    val data = Map(
      "vce/vce/vce_pod" -> Map(
        "externalEmailRecipients" -> "saumitra.srivastav7@gmail.com",
        "internalEmailRecipients" -> "saumitra.srivastav7@gmail.com,saumitra.srivastav@glassbeam.com",
        "mandatoryEmailRecipients" -> "saumitra.srivastav7@gmail.com",
        "ignoreLoadId" -> ""
      ),
      "storvisor/storvisor/storvisor_pod" -> Map(
        "externalEmailRecipients" -> "saumitra.srivastav7@gmail.com",
        "internalEmailRecipients" -> "saumitra.srivastav7@gmail.com,saumitra.srivastav@glassbeam.com",
        "mandatoryEmailRecipients" -> "saumitra.srivastav7@gmail.com",
        "ignoreLoadId" -> ""
      )
    )
    data.getOrElse(mps,Map())
  }

  def get(mps:String, key:String):String = {
    val config = getMpsConfig(mps)
    config.getOrElse(key,"")
  }

}
