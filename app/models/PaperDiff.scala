package models

import play.api.libs.json.Json

case class PaperDiff(
  dModified: Option[Long],
  dTitle: Option[String],
  dPermissions: Option[String],
  pnTags: PNSet,
  pGroups: Vector[String],
  nGroups: Vector[String],
  pElements: Vector[String],
  nElements: Vector[String],
  origin: Vector[String]
) {
  def checkDistinct[T](a: Vector[T]) {
    assert(a.distinct.size == a.size, s"${a.getClass.getSimpleName} must contain distinct values")
  }
  Seq(pGroups, nGroups, pElements, nElements).map(checkDistinct)
  assert(pGroups.intersect(nGroups).isEmpty, "Groups: removed and added values must be different")
  assert(pElements.intersect(nElements).isEmpty, "Elements: removed and added values must be different")
}

object PaperDiff {
  implicit lazy val jsonFormat_PaperDiff = Json.format[PaperDiff]

  def patchWithTime(p: Paper, diff: PaperDiff, datetime: Long): Paper = {
    val title = diff.dTitle.getOrElse(p.title)
    val permissions = if (diff.dPermissions.isDefined) diff.dPermissions else p.permissions
    val tags = p.tags diff diff.pnTags.neg ++ diff.pnTags.pos
    val groups = p.groups diff diff.nGroups ++ diff.pGroups
    val elements = p.elements diff diff.nElements ++ diff.pElements

    Paper.apply(
      _id = p._id,
      title = title,
      tags = tags,
      created = p.created,
      modified = datetime,
      elements = elements,
      groups = groups,
      username = p.username,
      permissions = permissions,
      diffs = diff.copy(dModified =  Some(datetime)) +: p.diffs
    )
  }

  def mergeWithTime(datetime: Long, diffs: PaperDiff*): PaperDiff = {
    diffs.reduce(merge).copy(dModified =  Some(datetime))
  }

  def merge(d1: PaperDiff, d2: PaperDiff): PaperDiff = {
    val (old, latest) = {
      if (d1.dModified.isDefined && d2.dModified.isDefined) {
        if (d1.dModified.get < d2.dModified.get) {
          d1 -> d2
        } else {
          d2 -> d1
        }
      } else {
        d1 -> d2
      }
    }
    def preferSecond[T](a: Option[T], b: Option[T]): Option[T] = {
      b match {
        case Some(_) => b
        case _ => a
      }
    }

    val mergeWork = old.copy(
      dModified =  preferSecond(old.dModified, latest.dModified),
      dTitle = preferSecond(old.dTitle, latest.dTitle),
      dPermissions =  preferSecond(old.dPermissions, latest.dPermissions),
      pnTags = old.pnTags.merge(latest.pnTags),
      pGroups = (old.pGroups ++ latest.pGroups).distinct,
      nGroups = (old.nGroups ++ latest.nGroups).distinct,
      pElements = (old.pElements ++ latest.pElements).distinct,
      nElements = (old.nElements ++ latest.nElements).distinct,
      origin =  (old.origin ++ latest.origin).distinct
    )
    val sharedGroups = mergeWork.pGroups intersect mergeWork.nGroups
    val sharedElements = mergeWork.pElements intersect mergeWork.nElements

    val finalDiff = mergeWork.copy(
      pGroups = mergeWork.pGroups diff sharedGroups,
      nGroups = mergeWork.nGroups diff sharedGroups,
      pElements = mergeWork.pElements diff sharedElements,
      nElements = mergeWork.nElements diff sharedElements
    )
    finalDiff
  }
}
