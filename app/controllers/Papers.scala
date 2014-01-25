package controllers

import play.api._
import play.api.mvc._
import java.util.UUID
import services.PaperDAO
import models.{Paper, JsonResult}

object Papers extends Controller {

  def index = Action {
    val list = (0 to 100).toList
    Ok(views.html.index2(list, "sadfasdf"))
  }

  def paperView(id: String) = Action.async {
    implicit req =>
      for {
        p <- PaperDAO.findById(id)
      } yield {
        p match {
          case Some(p) =>
            Ok(JsonResult.success(p))
          case None =>
            BadRequest(JsonResult.error("Paper not found"))
        }
      }
  }

  def paperSubmit(id: String) = Action.async {
    implicit req =>
      for {
        p <- PaperDAO.findById(id).map(_.map(Paper.json2model))
      } yield {
        (p, req.body.asJson.map(Paper.json2model)) match {
          case (Some(oldpaper), Some(newpaper)) =>

        }
        p match {
          case Some(p) =>
            Ok(p)
          case None =>
            BadRequest().
              flashing("error" -> "Paper not found")
        }
      }
  }

  def paperNew = Action {
    implicit req =>
      val id = UUID.randomUUID().toString
  //    val newPaper = Paper.createBlank(id)
  //    PaperDAO.save(newPaper)
  //    Ok(Json.obj("status" -> "success", "data" -> newPaper.toJson))
      ???
  }

  def recentPaperids = TODO
}