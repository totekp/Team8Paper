package controllers

import play.api._
import play.api.mvc._
import java.util.UUID

object Papers extends Controller {

  def index = Action {
    val list = (0 to 100).toList
    Ok(views.html.index2(list, "sadfasdf"))
  }

  def paperView(id: String) = play.mvc.Results.TODO
  
  def paperSubmit(id: String) = play.mvc.Results.TODO

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