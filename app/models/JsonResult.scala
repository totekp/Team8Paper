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

  def success(data: JsValueWrapper, data2: Option[JsValueWrapper] = None) = {
    Results.Ok {
      Json.obj(
        "status" -> "success",
        "data" -> data
      ) ++ {
        data2 match {
          case Some(j) =>
            Json.obj("data2" -> j)
          case None =>
            Json.obj()
        }
      }
    }
  }

  def jsonSuccess(data: JsValueWrapper) = {
      Json.obj(
        "status" -> "success",
        "data" -> data
      )
  }
}
