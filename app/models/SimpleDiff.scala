package models

import play.api.libs.json.Json
import scala.concurrent.duration.Duration

case class SimpleDiff(
  modified: Long,
  message: String,
  origin: Vector[String]
) {

}

object SimpleDiff {
  implicit lazy val jsonFormat_SimpleDiff = Json.format[SimpleDiff]

  def create(message: String, origin: Vector[String], time: Long) =
    SimpleDiff(time, message, origin)

  def reduce(prev: SimpleDiff, curr: SimpleDiff): SimpleDiff = {
    if (prev.message.trim.isEmpty)
      curr
    else if (curr.message.trim.isEmpty)
      prev
    else
      SimpleDiff(curr.modified, Vector(curr.message.split(';'), prev.message.split(';')).flatten.distinct.mkString(";"), (prev.origin ++ curr.origin).distinct)
  }

  def combine(interval: Duration, diffs: Vector[SimpleDiff]): Vector[SimpleDiff] = {
    val descDiffs = diffs.sortBy(- _.modified)
    descDiffs.foldLeft(Vector[SimpleDiff]()){
      case (Vector(), a) => Vector(a)
      case (vs, a) =>
        if (vs.head.modified - a.modified > interval.toMillis) {
          vs :+ a
        } else {
          reduce(a, vs.head) +: vs.tail
        }
    }
  }
}
