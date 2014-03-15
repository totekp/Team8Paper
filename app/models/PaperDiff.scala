package models

import play.api.libs.json.{JsValue, Json}
import util.JsonUtil

case class PaperDiff(
  modified: Long,
  diff: JsDiff,
  origin: Vector[String]
) {
//  def reverse: PaperDiff = {
//    Paper(
//      newModified = newModified,
//      newTitle =
//    )
//  }
}

case class JsDiff(deleted: Vector[String], added: Map[String, JsValue])

object JsDiff {
  implicit lazy val jsonFormat_JsDiff = Json.format[JsDiff]

  def empty = JsDiff(Vector.empty, Map.empty)

}

object PaperDiff {
  implicit lazy val jsonFormat_PaperDiff = Json.format[PaperDiff]

  def empty(origin: Vector[String] = Vector.empty) = PaperDiff(System.currentTimeMillis(), JsDiff.empty, origin)
  def patch(p: Paper, diff: PaperDiff): Paper = {
    val pJ = Paper.model2json(p)

    ???
  }

  def mergeOldToNew(diffs: PaperDiff*): PaperDiff = {
    diffs.reduce(merge)
  }

  def merge(dOld: PaperDiff, dNew: PaperDiff): PaperDiff = {
    val oldJ = JsonUtil.mongo.createModQuery(dOld.diff.deleted, dOld.diff.added.toSeq)
    val newJ = JsonUtil.mongo.createModQuery(dNew.diff.deleted, dNew.diff.added.toSeq)
    val uJ = JsonUtil.mongo.merge(oldJ, newJ)
    val uDiff = JsDiff(
      JsonUtil.mongo.getDelete(uJ),
      JsonUtil.mongo.getAdded(uJ).toMap
    )
    PaperDiff(
      dNew.modified,
      uDiff,
      dOld.origin ++ dNew.origin // basic
    )
  }
}
