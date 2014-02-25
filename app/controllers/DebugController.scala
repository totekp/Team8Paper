package controllers

import play.api.mvc._
import util.Implicits._
import models.JsonResult
import services.PaperDAO
import play.api.libs.concurrent.Execution.Implicits._


object DebugController extends Controller {

  def clear = Action.async {
    implicit req =>
      PaperDAO.coll.drop().map {
        _ =>
          Redirect(routes.Papers.index())
      }
  }

}