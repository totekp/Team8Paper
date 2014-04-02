package controllers

import play.api.mvc._
import services.{UsernameAuth, PaperDAO}
import models.{Paper, JsonResult}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.{JsObject, Json}
import util.{Aggregation, Generator}
import scala.concurrent.Future
import play.api.Logger
import util.Implicits._
import scala.collection.mutable
import common.Common._

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
              if (UsernameAuth.isOwner(p.username, session)) {
                val paperJson = Paper.model2json(p)
                Ok(views.html.paper(JsonResult.jsonSuccess(paperJson)))
              } else {
                JsonResult.noPermission
              }
            }
          case None =>
            JsonResult.error("Paper not found")
        }
      }
  }

  // For debug purposes
  def paperInfo(id: String) = Action.async {
    implicit req =>
      for {
        p <- PaperDAO.findByIdModel(id)
      } yield {
        p match {
          case Some(p) =>
            tryOrError {
              if (UsernameAuth.isOwner(p.username, session)) {
                val sb = mutable.StringBuilder.newBuilder

                var data = mutable.HashMap[String, String]()
                data += "id" -> p._id
                data += "title" -> p.title
                data += "username" -> p.username.toString
                data += "number of elements" -> p.elements.size.toString
                data += "number of groups" -> p.groups.size.toString
                data += "number of tags" -> p.tags.size.toString
                data += "permissions" -> p.permissions.toString
                data = data.map(t => t._1.capitalize -> t._2)
                sb.append(data.toList.sortBy(_._1).map(t => s"${t._1} -> ${t._2}").mkString("\n"))
                sb.append("\n\n")

                sb.append(Json.prettyPrint(PaperDAO.jsonable.model2json(p)))
                Ok(sb.toString)
              } else {
                JsonResult.noPermission
              }
            }
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
      def readPaper(p: Option[Paper]): Future[SimpleResult] = {
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
                if (UsernameAuth.isOwner(json getAsString Paper.username, session)) {
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
                } else {
                  Future.successful(
                    JsonResult.noPermission)
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
      val username = req.session.get("username")
      val newPaper = Paper.createBlank(id, username)
      PaperDAO.save(newPaper).map {
        le =>
          Redirect(routes.Papers.paperView(newPaper._id))
      }
  }

  def recentPaperShorts = Action.async {
    implicit req =>
      val username = req.session.get("username")
      val q = username.map(u =>
        Json.obj(Paper.username -> u))
        .getOrElse(Json.obj(Paper.username -> Json.obj("$exists" -> false)))

      val papers = PaperDAO.find(q, Json.obj(Paper.modified -> -1), 25)
      for {
        papersJson <- papers
      } yield {
        JsonResult.success(papersJson.map(j => j)) // TODO strip data
      }
  }

  import util.Implicits._

  def getPaper = Action.async {
    implicit req =>
      req.body.asJson match {
        case Some(j) =>
          tryOrError {
            val id = j asString "_id"
            val username = j getAsString "username"
            for {
              p <- PaperDAO.findByIdModel(id)
            } yield {
              val paperJson = Paper.model2json(
                  p.getOrElse(throw new Exception("Paper not found")))
              if (UsernameAuth.isOwner(p.get.username, username)) {
                JsonResult.success(paperJson)
              } else {
                JsonResult.noPermission
              }
            }
          }
        case None =>
          Future.successful(
            JsonResult.error("Invalid input")
          )
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
                    if (UsernameAuth.isOwner(paper.username, req.session.get("username"))) {
                      val newPaper = paper.copy(
                        _id = newId,
                        created = nowms,
                        modified = nowms)
                      PaperDAO.save(newPaper, ow = false).map {
                        le =>
                          newId
                      }
                    } else {
                      throw new Exception("No permission")
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
            val tagQ = {
              req.session.get("username").map {
                u =>
                  Json.obj(
                    Paper.username -> u,
                    Paper.tags -> Json.obj("$all" -> searchTags)
                  )
              }.getOrElse {
                Json.obj(Paper.tags -> Json.obj("$all" -> searchTags))
              }
            }
            for {
              r <- PaperDAO.find(tagQ, Json.obj(Paper.modified -> -1))
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
            val username = req.session.get("username")
            val q = username.map(u =>
              Json.obj(Paper._id -> paperId, Paper.username -> u))
              .getOrElse(Json.obj(Paper._id -> paperId))
            for {
              a <- PaperDAO.findOneModel(q)
              r <- {
                a match {
                  case Some(p) =>
                    if (UsernameAuth.isOwner(p.username, username)) {
                      PaperDAO.remove(q).map {
                        _ =>
                          JsonResult.success("")
                      }
                    } else {
                      Future.successful {
                        JsonResult.noPermission
                      }
                    }
                  case None =>
                    Future.successful(
                      JsonResult.error("Paper not found")
                    )
                }
              }
            } yield {
              r
            }
          }
        case None =>
          Future.successful(JsonResult.error("Invalid input"))
      }
  }

  def tagCloud = Action.async {
    implicit req =>
      val fm = Aggregation.tagCloud(req.session.get("username"))
      for {
        m <- fm
      } yield {
        val desc = m.toVector.sortBy(_._2).reverse.map(_.swap).map(t =>
          Json.obj(
            "_id" -> t._2,
            "count" -> t._1))
        JsonResult.success(desc)
      }
  }


}