package models

import play.api.libs.json.{JsValue, Json}

object JsonResult {
  def error(data: String) = {
    Json.obj(
      "status" -> "error",
      "data" -> data)
  }

  def success(data: JsValue) = {
    Json.obj(
      "status" -> "success",
      "data" -> data
    )
  }
}
