package controllers.common

import play.api.mvc.SimpleResult
import play.api.Logger
import models.JsonResult
import scala.concurrent.Future

object Common {
  def tryOrError(block: => SimpleResult) = {
    try {
      block
    } catch {
      case e: Exception =>
        Logger.error(e.getStackTraceString)
        JsonResult.error(e.getMessage)
    }
  }

  def tryOrError(block: => Future[SimpleResult]) = {
    try {
      block
    } catch {
      case e: Throwable =>
        Logger.error(e.getStackTraceString)
        Future.successful(JsonResult.error(e.getMessage))
    }
  }

}
