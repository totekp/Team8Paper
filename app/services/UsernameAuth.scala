package services

import models.Paper
import play.api.mvc.Session

object UsernameAuth {

  def hasView(paperUsername: Option[String], clientUsername: Option[String]): Boolean = {
    (paperUsername, clientUsername) match {
      case (Some(a), Some(b)) =>
        a == b
      case (None, None) =>
        true
      case _ =>
        false
    }
  }

  def hasUsername(paperUsername: Option[String], s: Session): Boolean = {
    hasView(paperUsername, s.get("username"))
  }



}
