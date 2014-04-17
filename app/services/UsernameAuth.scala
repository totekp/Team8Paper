package services

import models.Paper
import play.api.mvc.Session
import clientmodels.ClientSession

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

  def canView(paperUsername: Option[String], cs: Option[ClientSession]): Boolean = {
    val r = for {
      au <- paperUsername
      bu <- cs.map(_.username)
    } yield {
      au == bu
    }
    r.getOrElse(false)
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
