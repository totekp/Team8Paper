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

    val groupAdditions = diff.diffGroups
      .additions
      .map(Group.jsonString2model)
    val groupDeletions = diff.diffGroups
      .deletions
      .map(Group.jsonString2model)
    val groups = p.groups -- groupDeletions ++ groupAdditions

    val elementAdditions = diff.diffElements
      .additions
      .map(Element.jsonString2model)
    val elementDeletions = diff.diffElements
      .deletions
      .map(Element.jsonString2model)
    val elements = p.elements -- elementDeletions ++ elementAdditions

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
      diffGroups = old.diffGroups.merge(latest.diffGroups),
      diffElements = old.diffElements.merge(latest.diffElements),
      origin =  (old.origin ++ latest.origin).distinct
    )
    // TODO unique by id only
    mergeWork
  }
}
