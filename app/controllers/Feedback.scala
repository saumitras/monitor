package controllers

import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import javax.imageio.ImageIO
import javax.xml.bind.DatatypeConverter

import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods.{parse => jparse}

import play.api.mvc.{Controller, Action}


object Feedback extends Controller {
  implicit private val formats = DefaultFormats

  def parsefeedback() = Action {request =>
    val feedback = request.body.asFormUrlEncoded.get("feedback")(0)
    val user = request.body.asFormUrlEncoded.get("user")(0)

    val data = jparse(feedback)
    val keys = data.extract[Map[String,Any]].keys.toList

    println(keys)
    val img = (data \ "img").extract[String]
    val note = (data \ "note").extract[String]
    val url = (data \ "url").extract[String]
    val browser = data \ "browser"
    val userAgent = (browser \ "userAgent").extract[String]
    val platform = (browser \ "platform").extract[String]
    //val html = (data \\ "html").extract[String]

    try {
      val img64 = img.replace("data:image/png;base64,","")
      val decodedBytes = DatatypeConverter.parseBase64Binary(img64)
      val bfi:BufferedImage = ImageIO.read(new ByteArrayInputStream(decodedBytes))
      val imgPath = "/tmp/screenshot_" + (System.currentTimeMillis() / 1000) + ".png"
      val outputFile = new File(imgPath)
      ImageIO.write(bfi , "png", outputFile)
      bfi.flush()

      sendFeedBackMail(imgPath, note, url, userAgent, platform, user)

      Ok("0")
    } catch {
      case ex:Exception =>
        ex.printStackTrace()
        InternalServerError("1")
    }

  }

  def sendFeedBackMail(imgPath:String, note:String, url:String, userAgent:String, platform:String, user:String) = {
    val body = s"<strong>URL:</strong> $url" +
      s"<strong>URL:</strong>UserAgent: $userAgent" +
      s"<strong>Platform:</strong> $platform" +
      s"<strong>Description:</strong> $note"

    val title = s"New feedback from $user"

    println(s"Title = $title")
    println(body)
  }

}
