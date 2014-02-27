package services

import models.Paper
import play.api.mvc.Session

object UsernameAuth {

  def isOwner(paperUsername: Option[String], clientUsername: Option[String]): Boolean = {
    (paperUsername, clientUsername) match {
      case (Some(a), Some(b)) =>
        a == b
      case (None, None) =>
        true
      case _ =>
        false
    }
  }

  def isOwner(paperUsername: Option[String], s: Session): Boolean = {
    isOwner(paperUsername, s.get("username"))
  }

  def isReadPublic(permissions: Option[String]) = {
    permissions match {
      case Some(s) =>
        s.contains("-rp")
      case None =>
        false
    }
  }



}
