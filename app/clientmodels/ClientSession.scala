package clientmodels

import play.api.mvc.{RequestHeader, Session}

case class ClientSession(
  username: String,
  created: Long = System.currentTimeMillis()
) {
  def toSession = {
    Session {
      Map(
        "username" -> username,
        "created" -> created.toString
      )
    }
  }
}

object ClientSession {

  def fromSession(session: Session): Option[ClientSession] = {
    for {
      username <- session.get("username")
      created <- session.get("created").map(_.toLong)
    } yield {
      ClientSession(username, created)
    }
  }

  def fromReq(implicit rh: RequestHeader): Option[ClientSession] = fromSession(rh.session)
}
