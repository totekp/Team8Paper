package models

import play.api.libs.json.Json

case class PaperDiff(
  dModified: Option[Long],
  dTitle: Option[String],
  dPermissions: Option[String],
  pTags: Vector[String],
  nTags: Vector[String],
  pGroups: Vector[String],
  nGroups: Vector[String],
  pElements: Vector[String],
  nElements: Vector[String],
  origin: Vector[String]
) {
  def checkDistinct[T](a: Vector[T]) {
    assert(a.distinct.size == a.size, s"${a.getClass.getSimpleName} must contain distinct values")
  }
  Seq(pTags, nTags, pGroups, nGroups, pElements, nElements).map(checkDistinct)
  assert(pTags.intersect(nTags).isEmpty, "Tags: removed and added values must be different")
  assert(pGroups.intersect(nGroups).isEmpty, "Groups: removed and added values must be different")
  assert(pElements.intersect(nElements).isEmpty, "Elements: removed and added values must be different")
}

object PaperDiff {
  lazy val jsonFormat_PaperDiff = Json.format[PaperDiff]

  def patch(p: Paper, diff: PaperDiff): Paper = {
    val modified = diff.dModified.getOrElse(System.currentTimeMillis())
    val title = diff.dTitle.getOrElse(p.title)
    val permissions = if (diff.dPermissions.isDefined) diff.dPermissions else p.permissions
    val tags = p.tags diff diff.nTags ++ diff.pTags
    val groups = p.groups diff diff.nGroups ++ diff.pGroups
    val elements = p.elements diff diff.nElements ++ diff.pElements

    Paper.apply(
      _id = p._id,
      title = title,
      tags = tags,
      created = p.created,
      modified = modified,
      elements = elements,
      groups = groups,
      username = p.username,
      permissions = permissions,
      diffs = diff +: p.diffs
    )
  }

  def merge(diffs: PaperDiff*): PaperDiff = {
    diffs.reduce(merge)
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
      pTags = (old.pTags ++ latest.pTags).distinct,
      nTags = (old.nTags ++ latest.nTags).distinct,
      pGroups = (old.pGroups ++ latest.pGroups).distinct,
      nGroups = (old.nGroups ++ latest.nGroups).distinct,
      pElements = (old.pElements ++ latest.pElements).distinct,
      nElements = (old.nElements ++ latest.nElements).distinct,
      origin =  (old.origin ++ latest.origin).distinct
    )
    val sharedTags = mergeWork.pTags intersect mergeWork.nTags
    val sharedGroups = mergeWork.pGroups intersect mergeWork.nGroups
    val sharedElements = mergeWork.pElements intersect mergeWork.nElements

    val finalDiff = mergeWork.copy(
      pTags = mergeWork.pTags diff sharedTags,
      nTags = mergeWork.nTags diff sharedTags,
      pGroups = mergeWork.pGroups diff sharedGroups,
      nGroups = mergeWork.nGroups diff sharedGroups,
      pElements = mergeWork.pElements diff sharedElements,
      nElements = mergeWork.nElements diff sharedElements
    )
    finalDiff
  }
}
