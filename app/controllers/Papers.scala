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
            Future.successful(
              JsonResult.error("Old paper not found"))
          case Some(oldpaper) =>
            req.body.asJson match {
              case None =>
                Future.successful(
                  JsonResult.error("Input is not a valid json"))
              case Some(json) =>
                val newPaper = Paper.json2model(json)
                if (oldpaper._id != newPaper._id) {

                  Future.successful(
                    JsonResult.error("Oldpaper and newpaper ids are not equal"))
                } else if (oldpaper == newPaper) {

                  Future.successful(
                    JsonResult.error("Paper not changed"))
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
          try {
            val id = j asString "_id"
            for {
              p <- PaperDAO.findByIdModel(id)
            } yield {
              val paperJson = Paper.model2json(
                p.getOrElse(throw new Exception("Paper not found")))
              JsonResult.success(paperJson)
            }
          } catch {
            case e: Exception =>
              Future(JsonResult.error(e.getMessage))
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
          try {
            val paper = j \ "paper"
            PaperDAO.save(Paper.json2model(paper), ow = true).map {
              le =>
                JsonResult.success("")
            }
          } catch {
            case e: Exception =>
              Future(JsonResult.error(e.getMessage))
          }
        case None =>
          Future.successful(JsonResult.error("Invalid json input"))
      }
  }

  def duplicatePaper = Action.async {
    implicit req =>
      req.body.asJson match {
        case Some(j) =>
          try {
            val oldId = j asString "_id"
            for {
              paper <- PaperDAO.findByIdModel(oldId)
              newid <- {
                paper match {
                  case Some(paper) =>
                    val newId = Generator.oid()
                    val nowms = System.currentTimeMillis()
                    val newPaper = paper.copy(
                        _id = newId,
                        created = nowms,
                        lastUpdated = nowms)
                    PaperDAO.save(newPaper, ow = false).map {
                      le =>
                        newId
                    }
                  case None =>
                   throw new Exception("Paper not found")
                }
              }
            } yield {
              JsonResult.success(newid)
            }

          } catch {
            case e: Exception =>
              Future(JsonResult.error(e.getMessage))
          }
        case None =>
          Future.successful(JsonResult.error("Invalid input"))
      }
  }

  def searchTags = Action.async {
    implicit req =>
      req.body.asJson match {
        case Some(j) =>
          try {
            val searchTags = (j \ "tags").as[Vector[String]]
            val tagQ = Json.obj(Paper.tags -> Json.obj("$all" -> searchTags))
            for {
              r <- PaperDAO.findModel(tagQ, Json.obj(Paper.lastUpdated -> -1))
            } yield {
              JsonResult.success(r.map(_._id))
            }
          } catch {
            case e: Exception =>
              Future(JsonResult.error(e.getMessage))
          }

        case None =>
          Future.successful(JsonResult.error("Invalid input"))
      }
  }

  def deletePaper = Action.async {
    implicit req =>
      req.body.asJson match {
        case Some(j) =>
          try {
            val paperId = j asString "_id"
            for {
              le <- PaperDAO.removeById(paperId)
            } yield {
              JsonResult.success("")
            }
          } catch {
            case e: Exception =>
              Future(JsonResult.error(e.getMessage))
          }
        case None =>
          Future.successful(JsonResult.error("Invalid input"))
      }
  }
}