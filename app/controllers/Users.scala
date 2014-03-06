package controllers

import play.api.mvc._
import util.Implicits._
import models.{User, JsonResult}
import scala.concurrent.Future
import common.Common._
import play.api.libs.json.Json
import services.UserDAO
import play.api.libs.concurrent.Execution.Implicits._
import util.PasswordCrypto


object Users extends Controller {

  def loginNoPassword = Action {
    implicit req =>
      req.body.asJson match {
        case Some(j) =>
          try {
            val username = j asString "username"

            JsonResult.success("Login success")
              .withSession("username" -> username)
          } catch {
            case e: Exception =>
              JsonResult.error(e.getMessage)
          }
        case None =>
          JsonResult.error("Invalid json input")
      }
  }

  def login = Action.async {
    implicit req =>
      req.body.asJson match {
        case Some(j) =>
          tryOrError {
            val username = j asString "username"
            val password = j asString "password"

            val userQ = Json.obj(User.username -> username)
            val fUser = UserDAO.findOneModel(userQ)
            for {
              optUser <- fUser
              r <- optUser match {
                case Some(u) =>
                  if (PasswordCrypto.checkPassword(u.salt, password, u.hash)) {
                    Future.successful(
                      JsonResult.success("Login successful")
                        .withSession("username" -> username)
                    )
                  } else {
                    Future.successful(
                      JsonResult.error("Incorrect password")
                    )
                  }
                case None =>
                  throw new Exception("Username does not exist")
              }
            } yield {
              r
            }
          }
        case None =>
          Future.successful(
            JsonResult.error("Invalid json input")
          )
      }
  }

  def register = Action.async {
    implicit req =>
      req.body.asJson match {
        case Some(j) =>
          tryOrError {
            val username = j asString "username"
            val password = j asString "password"

            val userQ = Json.obj(User.username -> username)
            val fUser = UserDAO.findOne(userQ)
            val r = for {
              optUser <- fUser
              r <- optUser match {
                case Some(u) =>
                  throw new Exception("Username already exists")
                case None =>
                  val user = User.create(username, password, None)
                  UserDAO.save(user, ow = false).map {
                    le =>
                      JsonResult.success("User registered successfully")
                  }
              }
            } yield {
              r
            }
            r
          }
        case None =>
          Future.successful(
            JsonResult.error("Invalid json input")
          )
      }
  }

  def logout = Action {
    implicit req =>
      JsonResult.success("Logout success")
        .withNewSession
  }

}