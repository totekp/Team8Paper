package models

import play.api.libs.json.Json

case class DiffSet(
  additions: Set[String],
  deletions: Set[String]
) {
  assert(additions.intersect(deletions).isEmpty, "Additions and Deletions must be different")

  def add(as: String*): DiffSet = {
    val aSet = as.toSet
    val shared = aSet intersect deletions
    this.copy(
      additions = additions ++ aSet -- shared,
      deletions = deletions -- shared
    )
  }

  def delete(ds: String*): DiffSet = {
    val dSet = ds.toSet
    val shared = dSet intersect additions
    this.copy(
      additions = additions -- shared,
      deletions = deletions ++ dSet -- shared
    )
  }

  def merge(other: DiffSet): DiffSet = {
    val as = additions ++ other.additions
    val ds = deletions ++ other.deletions
    val shared = as intersect ds
    this.copy(
      additions = as -- shared,
      deletions = ds -- shared
    )
  }
}

object DiffSet {
  implicit lazy val jsonFormat_PNSet = Json.format[DiffSet]

  def create(additions: Set[String], deletions: Set[String]): DiffSet = {
    val shared = additions.intersect(deletions)
    DiffSet(additions -- shared, deletions -- shared)
  }
}
