package controllers

import play.api._
import play.api.mvc._
import java.util.UUID
import services.PaperDAO
import models.{Paper, JsonResult}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json

object Papers extends Controller {

  def index = Action {

    Ok(views.html.index2())
    //Ok(views.html.paper())
  }

  /**
   * Load a saved paper
   * @param id
   * @return
   */
  def paperView(id: String) = Action.async {
    implicit req =>
      for {
        p <- PaperDAO.findById(id)
      } yield {
        p match {
          case Some(p) =>
            Ok(views.html.paper(JsonResult.jsonSuccess(p)))
          case None =>
            JsonResult.error("Paper not found")
        }
      }
  }
  /**
   * Save paper
   * @param id
   * @return
   */
  def paperSubmit(id: String) = Action.async {
    implicit req =>
      for {
        p <- PaperDAO.findById(id).map(_.map(Paper.json2model))
      } yield {
        p match {
          case None =>
            JsonResult.error("Old paper not found")
          case Some(oldpaper) =>
            println(req.body.asJson);
            req.body.asJson match {
              case None =>
                JsonResult.error("Input is not a valid json")
              case Some(json) =>
                val paper = Paper.json2model(json)
                    if (oldpaper.id != paper.id) {
                      JsonResult.error("Oldpaper and newpaper ids are not equal")
                    } else {
                      PaperDAO.save(paper, ow = true)
                      JsonResult.success("Paper saved")
                    }
            }
        }
      }
  }

  def paperNew = Action.async {
    implicit req =>
      val id = UUID.randomUUID().toString
      val newPaper = Paper.createBlank(id)
      PaperDAO.save(newPaper).map {
        le =>
          Redirect(routes.Papers.paperView(newPaper.id))
      }
  }

  def recentPaperids = Action.async {
    implicit req =>
      val papers = PaperDAO.findModel(Json.obj(), 25)
      for {
        papers <- papers
      } yield {
        JsonResult.success(papers.sortBy(_.lastupdated).reverse.map(Paper.model2json))
      }
  }

}