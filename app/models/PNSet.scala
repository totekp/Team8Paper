package models

import play.api.libs.json.Json

case class PNSet(
  pos: Set[String],
  neg: Set[String]
) {
  assert(pos.intersect(neg).isEmpty, "Pos and Neg must contain different elements.")

  def addPos(ps: String*): PNSet = {
    this.copy(pos = pos ++ ps.toSet).cancelPN()
  }

  def addNeg(ns: String*): PNSet = {
    this.copy(neg = neg ++ ns.toSet).cancelPN()
  }

  def merge(other: PNSet): PNSet = {
    this.copy(
      pos = pos ++ other.pos,
      neg = neg ++ other.neg
    ).cancelPN()
  }

  def cancelPN(): PNSet = {
    val shared = pos intersect neg
    this.copy(
      pos = pos -- shared,
      neg = neg -- shared
    )
  }
}

object PNSet {
  implicit lazy val jsonFormat_PNSet = Json.format[PNSet]

  def create(ps: Seq[String], ns: Seq[String]): PNSet = {
    PNSet(ps.toSet, ns.toSet).cancelPN()
  }
}
