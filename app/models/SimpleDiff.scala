package models

import play.api.libs.json.Json
import scala.concurrent.duration.Duration
import play.api.Logger

case class SimpleDiff(
  modified: Long,
  message: String,
  origin: Vector[String]
) {

}

object SimpleDiff {
  implicit lazy val jsonFormat_SimpleDiff = Json.format[SimpleDiff]

  def create(message: Vector[String], origin: Vector[String], time: Long) =
    SimpleDiff(time, message.mkString(";"), origin)

  def reduce(prev: SimpleDiff, curr: SimpleDiff): SimpleDiff = {
    if (prev.message.trim.isEmpty)
      curr
    else if (curr.message.trim.isEmpty)
      prev
    else
      SimpleDiff(
        prev.modified,
        Vector(
            prev.message.split(';'),
            curr.message.split(';')
        ).flatten.distinct.mkString(";"),
        (prev.origin ++ curr.origin).distinct
      )
  }

  def combine(interval: Duration, diffs: Vector[SimpleDiff]): Vector[SimpleDiff] = {
    val descDiffs = diffs.sortBy(_.modified)

    descDiffs.foldLeft(Vector[SimpleDiff]()){
      case (Vector(), a) => Vector(a)
      case (vs, a) =>
        if (math.abs(a.modified - vs.last.modified) > interval.toMillis) {
          vs :+ a
        } else {
          reduce(vs.head, a) +: vs.tail
        }
    }
  }
}
