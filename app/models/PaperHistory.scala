package models

import play.api.libs.json.Json

case class PaperHistory(
  datetime: Long,
  origin: Option[String],
  changes: Vector[String]
)

object PaperHistory {
  lazy val jsonFormat_PaperHistory = Json.format[PaperHistory]

  def anony(chamges: Vector[String]) = {
    PaperHistory(System.currentTimeMillis(), None, chamges)
  }

  def withOrigin(origin: String, changes: Vector[String]) = {
    PaperHistory(System.currentTimeMillis(), Some(origin), changes)
  }

  def getHistory(oldPaper: Paper, newPaper: Paper, origin: Option[String] = None): Option[PaperHistory] = {
    if (oldPaper._id != newPaper._id) {
      throw new Exception("Paper _id cannot be changed")
    }
    if (oldPaper.history != newPaper.history) {
      throw new Exception("Paper history is already modified")
    }
    val now = System.currentTimeMillis()
    var changes: List[String] = Nil
    if (oldPaper.title != newPaper.title) {
      changes ::= s"Title changed: ${oldPaper.title} -> ${newPaper.title}"
    }

    ???
  }
}
