package controllers

import play.api._
import play.api.mvc._
import java.util.UUID
import services.PaperDAO
import models.{Paper, JsonResult}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import util.Generator
import scala.concurrent.Future

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
        p <- PaperDAO.findByIdModel(id)
      } yield {
        p match {
          case Some(p) =>
            val paperJson = Paper.model2json(p)
            Ok(views.html.paper(JsonResult.jsonSuccess(paperJson)))
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
      def readPaper(p: Option[Paper]) = {
        p match {
          case None =>
            Future.successful(JsonResult.error("Old paper not found"))
          case Some(oldpaper) =>
            req.body.asJson match {
              case None =>
                Future.successful(JsonResult.error("Input is not a valid json"))
              case Some(json) =>
                val newPaper = Paper.json2model(json)
                if (oldpaper._id != newPaper._id) {

                  Future.successful(JsonResult.error("Oldpaper and newpaper ids are not equal"))
                } else if (oldpaper == newPaper) {

                  Future.successful(JsonResult.error("Paper not changed"))
                } else {

                  val newPaperUpdatedTime = newPaper.updatedTime()
                  PaperDAO.save(newPaperUpdatedTime, ow = true).map {
                    le =>
                      JsonResult.success("Paper saved")
                  }
                }
            }
        }
      }
      for {
        p <- PaperDAO.findByIdModel(id)
        r <- readPaper(p)
      } yield {
        r
      }
  }

  def paperNew = Action.async {
    implicit req =>
      val id = Generator.oid()
      val newPaper = Paper.createBlank(id)
      Future(Ok("" + Paper.model2json(newPaper)))
      PaperDAO.save(newPaper).map {
        le =>
          Redirect(routes.Papers.paperView(newPaper._id))
      }
  }

  def recentPaperids = Action.async {
    implicit req =>
      val papers = PaperDAO.find(Json.obj(), Json.obj(Paper.lastUpdated -> -1), 25)
      for {
        papersJson <- papers
      } yield {
        JsonResult.success(papersJson)
      }
  }

  import util.Implicits._
  def getPaper = Action.async {
    implicit req =>
      req.body.asJson match {
        case Some(j) =>
          val id = j asString "_id"
          for {
            p <- PaperDAO.findByIdModel(id)
          } yield {
            p match {
              case Some(p) =>
                val paperJson = Paper.model2json(p)
                JsonResult.success(paperJson)
              case None =>
                JsonResult.error("Paper not found")
            }
          }
        case None =>
          Future.successful(
            JsonResult.error("Invalid input")
          )
      }
  }

  def savePaper = Action.async {
    implicit req =>
      req.body.asJson match {
        case Some(j) =>
          val paper = j \ "paper"
          PaperDAO.save(Paper.json2model(paper), true).map {
            le =>
              JsonResult.success("")
          }
        case None =>
          Future.successful(JsonResult.error("Invalid json input"))
      }
  }

  def duplicatePaper = Action.async {
    implicit req =>
      req.body.asJson match {
        case Some(j) =>
          val oldId = j asString "_id"
          val paper = PaperDAO.findByIdModel(oldId)
          for {
            paper <- paper
          } yield {
            paper match {
              case Some(paper) =>
                val newId = Generator.oid()
                PaperDAO.save(paper.copy(_id = newId), false)
                JsonResult.success(newId)
              case None =>
                JsonResult.error("Paper not found")
            }
          }
        case None =>
          Future.successful(JsonResult.error("Invalid json input"))
      }
  }

  def searchTags = Action.async {
    implicit req =>
      ???
  }
}