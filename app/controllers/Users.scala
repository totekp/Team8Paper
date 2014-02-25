package controllers

import play.api.mvc._
import util.Implicits._
import models.JsonResult

object Users extends Controller {

  def login = Action {
    implicit req =>
      req.body.asJson match {
        case Some(j) =>
          try {
            val username = j asString "username"

            Redirect(routes.Papers.index())
              .withSession("username" -> username)
          } catch {
            case e: Exception =>
              JsonResult.error(e.getMessage)
          }
        case None =>
          JsonResult.error("Invalid json input")
      }
  }

  def logout = Action {
    implicit req =>
      Redirect(routes.Papers.index()).withNewSession
  }

}