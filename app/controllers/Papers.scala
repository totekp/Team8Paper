package controllers

import play.api.mvc._
import services.{UsernameAuth, PaperDAO}
import models.{Paper, JsonResult}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import util.Generator
import scala.concurrent.Future
import play.api.Logger

object Papers extends Controller {

  def index = Action {
    implicit req =>
      Ok(views.html.index2())
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
            tryOrError {
              if (UsernameAuth.hasUsername(p.username, session)) {
                val paperJson = Paper.model2json(p)
                Ok(views.html.paper(JsonResult.jsonSuccess(paperJson)))
              } else {
                JsonResult.error("Forbidden. Are you logged in?")
              }
            }
          case None =>
            JsonResult.error("Paper not found")
        }
      }
  }

  private def tryOrError(block: => SimpleResult) = {
    try {
      block
    } catch {
      case e: Exception =>
        Logger.error(e.getStackTraceString)
        JsonResult.error(e.getMessage)
    }
  }

  private def tryOrError(block: => Future[SimpleResult]) = {
    try {
      block
    } catch {
      case e: Exception =>
        Logger.error(e.getStackTraceString)
        Future.successful(JsonResult.error(e.getMessage))
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
      val newPaper = Paper.createBlank(id, None)
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
          tryOrError {
            val id = j asString "_id"
            for {
              p <- PaperDAO.findByIdModel(id)
            } yield {
              val paperJson = Paper.model2json(
                p.getOrElse(throw new Exception("Paper not found")))
              JsonResult.success(paperJson)
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
          tryOrError {
            val paper = j \ "paper"
            PaperDAO.save(Paper.json2model(paper), ow = true).map {
              le =>
                JsonResult.success("")
            }
          }
        case None =>
          Future.successful(JsonResult.error("Invalid json input"))
      }
  }

  def duplicatePaper = Action.async {
    implicit req =>
      req.body.asJson match {
        case Some(j) =>
          tryOrError {
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

          }
        case None =>
          Future.successful(JsonResult.error("Invalid input"))
      }
  }

  def searchTags = Action.async {
    implicit req =>
      req.body.asJson match {
        case Some(j) =>
          tryOrError {
            val searchTags = (j \ "tags").as[Vector[String]]
            val tagQ = Json.obj(Paper.tags -> Json.obj("$all" -> searchTags))
            for {
              r <- PaperDAO.find(tagQ, Json.obj(Paper.lastUpdated -> -1))
            } yield {
              JsonResult.success(r)
            }
          }
        case None =>
          Future.successful(JsonResult.error("Invalid input"))
      }
  }

  def deletePaper = Action.async {
    implicit req =>
      req.body.asJson match {
        case Some(j) =>
          tryOrError {
            val paperId = j asString "_id"
            for {
              le <- PaperDAO.removeById(paperId)
            } yield {
              JsonResult.success("")
            }
          }
        case None =>
          Future.successful(JsonResult.error("Invalid input"))
      }
  }
}