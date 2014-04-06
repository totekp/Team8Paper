package controllers

import models.JsonDiff
import play.api.libs.json.Json

case class SimpleDiff(
  modified: Long,
  message: String,
  origin: Vector[String]
) {

}

object SimpleDiff {
  implicit lazy val jsonFormat_SimpleDiff = Json.format[SimpleDiff]

  def create(message: String, origin: Vector[String]) =
    SimpleDiff(System.currentTimeMillis, message, origin)
}
