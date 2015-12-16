package models.utils

object Util {

  def md5Hash(s: String) = {
    val m = java.security.MessageDigest.getInstance("MD5")
    val b = s.getBytes("UTF-8")
    m.update(b, 0, b.length)
    new java.math.BigInteger(1, m.digest()).toString(16)
  }

  def emailColsToTableRows(headerCols:List[String], rows:List[List[String]]) = {

    val borderStyleTable =  " style='border: 1px solid #000; border-collapse:collapse; '"
    val borderStyleHeader = " style='border: 1px solid #000; background-color: #333; color:#e7e7e7; '"
    val borderStyleAlt1  =  " style='border: 1px solid #000; background-color: #FFF; color:#000; '"
    val borderStyleAlt2  =  " style='border: 1px solid #000; background-color: #e7e7e7; color:#000; '"

    var body = s"<table $borderStyleTable>"

    body += s"<tr>" +
      s"<th $borderStyleHeader>#</th>" +
      headerCols.map( c => s"<th $borderStyleHeader>$c</th>").mkString +
      "</tr>"

    var counter = 0
    for(r <- rows) {
      counter += 1
      val selectedStyle = if(counter % 2 == 0) borderStyleAlt1 else borderStyleAlt2
      val tableRow = "<tr>" +
        s"<td $selectedStyle>" + counter + "</td>" +
        r.map(c => s"<td $selectedStyle>" + c + "</td>").mkString +
        "</tr>"
      body += tableRow

    }

    body += "</table>"

    body

  }


}
