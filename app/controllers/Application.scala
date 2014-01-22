package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    val list = (0 to 100).toList
    Ok(views.html.index2(list))
  }

  def paperView(id: String) = play.mvc.Results.TODO
  
  def paperSubmit(id: String) = play.mvc.Results.TODO
}