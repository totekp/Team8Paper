package models

import play.api.libs.json.Json

case class PaperDiff(
  newModified: Option[Long],
  newTitle: Option[String],
  newPermissions: Option[String],
  diffTags: DiffSet,
  diffGroups: DiffSet,
  diffElements: DiffSet,
  origin: Vector[String]
)

object PaperDiff {
  implicit lazy val jsonFormat_PaperDiff = Json.format[PaperDiff]

  def patchWithTime(p: Paper, diff: PaperDiff, datetime: Long): Paper = {
    val title = diff.newTitle.getOrElse(p.title)
    val permissions = if (diff.newPermissions.isDefined) diff.newPermissions else p.permissions
    val tags = p.tags diff diff.diffTags.deletions ++ diff.diffTags.additions
    val groups = p.groups // TODO diff diff.diffGroups.deletions ++ diff.diffGroups.additions
    val elements = p.elements // TODO diff diff.diffElements.deletions ++ diff.diffElements.additions

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
      diffs = diff.copy(newModified =  Some(datetime)) +: p.diffs
    )
  }

  def mergeWithTime(datetime: Long, diffs: PaperDiff*): PaperDiff = {
    diffs.reduce(merge).copy(newModified =  Some(datetime))
  }

  def merge(d1: PaperDiff, d2: PaperDiff): PaperDiff = {
    val (old, latest) = {
      if (d1.newModified.isDefined && d2.newModified.isDefined) {
        if (d1.newModified.get < d2.newModified.get) {
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
      newModified =  preferSecond(old.newModified, latest.newModified),
      newTitle = preferSecond(old.newTitle, latest.newTitle),
      newPermissions =  preferSecond(old.newPermissions, latest.newPermissions),
      diffTags = old.diffTags.merge(latest.diffTags),
//      pGroups = (old.pGroups ++ latest.pGroups).distinct,
//      nGroups = (old.nGroups ++ latest.nGroups).distinct,
//      pElements = (old.pElements ++ latest.pElements).distinct,
//      nElements = (old.nElements ++ latest.nElements).distinct,
      origin =  (old.origin ++ latest.origin).distinct
    )
//    val sharedGroups = mergeWork.pGroups intersect mergeWork.nGroups
//    val sharedElements = mergeWork.pElements intersect mergeWork.nElements

    val finalDiff = mergeWork.copy(
//      pGroups = mergeWork.pGroups diff sharedGroups,
//      nGroups = mergeWork.nGroups diff sharedGroups,
//      pElements = mergeWork.pElements diff sharedElements,
//      nElements = mergeWork.nElements diff sharedElements
    )
    finalDiff
  }
}
