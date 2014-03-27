package models

import play.api.libs.json.{JsValue, Json}
import util.JsonUtil

case class PaperDiff(
  modified: Long,
  diff: JsonDiff,
  origin: Vector[String]
) {
//  def reverse: PaperDiff = {
//    Paper(
//      newModified = newModified,
//      newTitle =
//    )
//  }
}

object PaperDiff {
  implicit lazy val jsonFormat_PaperDiff = Json.format[PaperDiff]

  def empty(origin: Vector[String] = Vector.empty) = PaperDiff(System.currentTimeMillis(), JsonDiff.empty, origin)
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
    val uDiff = JsonDiff(
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
