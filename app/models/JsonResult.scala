package models

import play.api.libs.json.{JsValue, Json}
import play.api.libs.json.Json.JsValueWrapper
import play.api.mvc.{SimpleResult, Results}

object JsonResult {
  def error(data: String) = {
    Results.Ok {
      Json.obj(
        "status" -> "error",
        "data" -> data)
    }
  }

  val noPermission: SimpleResult = {
    error("No permission")
  }

  def success(data: JsValueWrapper) = {
    Results.Ok {
      Json.obj(
        "status" -> "success",
        "data" -> data
      )
    }
  }

  def jsonSuccess(data: JsValueWrapper) = {
      Json.obj(
        "status" -> "success",
        "data" -> data
      )
  }
}
