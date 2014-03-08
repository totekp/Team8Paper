package models

import play.api.libs.json.Json

case class PaperHistory(
  datetime: Long,
  origin: Vector[String],
  changes: Vector[String]
)

object PaperHistory {
  lazy val jsonFormat_PaperHistory = Json.format[PaperHistory]

  def anony(changes: Vector[String]) = {
    PaperHistory(System.currentTimeMillis(), Vector.empty, changes)
  }

  def withOrigin(origin: Vector[String], changes: Vector[String]) = {
    PaperHistory(System.currentTimeMillis(), origin, changes)
  }

  def mergeHistories(hs: Seq[PaperHistory]): PaperHistory = {
    assert(hs.nonEmpty, "Cannot merge empty history")
    val sortedDesc = hs.sortBy(_.datetime).reverse
    sortedDesc.reduce {
      (acc, h) =>
        PaperHistory(
          acc.datetime,
          origin = acc.origin ++ h.origin, // TODO cancel out operations
          changes = acc.changes ++ h.changes
        )
    }
  }

  def getHistory(oldPaper: Paper, newPaper: Paper, origin: Vector[String] = Vector.empty): Either[String, PaperHistory] = {
    if (oldPaper.lastUpdated > newPaper.lastUpdated) {
      Left("Logic Error. Old paper is newer than new paper.")
    } else if (oldPaper._id != newPaper._id) {
      Left("Paper _id cannot be changed")
    } else if (!newPaper.history.containsSlice(oldPaper.history)) {
      Left("Paper history is already modified")
    } else {
      var changes = Vector[String]()
      val now = System.currentTimeMillis()
      if (oldPaper.title != newPaper.title) {
        changes = changes :+ s"Title changed: ${oldPaper.title} -> ${newPaper.title}"
      }
      {
        val removedTags = oldPaper.tags diff newPaper.tags
        val addedTags = newPaper.tags diff oldPaper.tags
        if (removedTags.nonEmpty || addedTags.nonEmpty)
          changes = changes :+
            s"Tag changed: ${addedTags.map(t => s"+$t").mkString(",")} ${removedTags.map(t => s"-$t").mkString(",",",","")}"
      }
      // TODO permission, users
      {
        val removedGroups = oldPaper.groups.diff(newPaper.groups)
        val addedGroups = newPaper.groups.diff(oldPaper.groups)
        if (removedGroups.nonEmpty) {
          changes = changes :+ "Removed groups:"
        }
        if (addedGroups.nonEmpty) {
          changes = changes :+ "Added groups:"
        }
      }
      {
        val removedElements = oldPaper.elements.diff(newPaper.elements)
        val addedElements = newPaper.elements.diff(oldPaper.elements)
        if (removedElements.nonEmpty) {
          changes = changes :+ "Removed elements:"
        }
        if (addedElements.nonEmpty) {
          changes = changes :+ "Added elements:"
        }
      }
      Right(
        PaperHistory(
          now,
          origin,
          changes
        )
      )
    }

  }
}
