package models

import play.api.libs.json.{Json, JsValue}


case class JsonDiff(deleted: Vector[String], added: Map[String, JsValue])

object JsonDiff {
  implicit lazy val jsonFormat_JsonDiff = Json.format[JsonDiff]

  def empty = JsonDiff(Vector.empty, Map.empty)

}
