package controllers

import play.api.mvc._
import util.Implicits._
import models.JsonResult
import services.{UserDAO, PaperDAO}
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.Future
import play.api.libs.json.{JsArray, Json}
import util.Aggregation


object DebugController extends Controller {

  def clear = Action.async {
    implicit req =>
      PaperDAO.coll.drop().map {
        _ =>
          Redirect(routes.Papers.index())
      }
//      Future.successful(Ok("Disabled"))
  }

  def users = Action.async {
    implicit req =>
      UserDAO.find(Json.obj(), Json.obj()).map(
        users =>
          Ok(Json.prettyPrint(JsArray(users)))
      )
  }

  def tagCloud = Action.async {
    implicit req =>
      val fm = Aggregation.tagCloud(req.session.get("username"))
      for {
        m <- fm
      } yield {
        val desc = m.toVector.sortBy(_._2).reverse.map(_.swap).mkString("\n")
        Ok(desc)
      }
  }

}