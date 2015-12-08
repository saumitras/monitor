package models.config

object CustomerConfig {

  def getMpsConfig(mps:String) = {
    val data = Map(
      "vce/vce/vce_pod" -> Map(
        "externalEmailRecipients" -> List("saumitra.srivastav7@gmail.com"),
        "internalEmailRecipients" -> List("saumitra.srivastav7@gmail.com"),
        "mandatoryEmailRecipients" -> List("saumitra.srivastav7@gmail.com"),
        "ignoreLoadId" -> List()
      )
    )
  }

  def get(mps:String, key:String) = {

  }

}
