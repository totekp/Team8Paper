package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok("Team8Paper Project")
  }

  def paperView(id: String) = play.mvc.Results.TODO
  
  def paperSubmit(id: String) = play.mvc.Results.TODO
}