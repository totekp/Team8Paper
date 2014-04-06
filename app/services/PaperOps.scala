package services

import util.JsonUtil
import scala.concurrent.duration._

object PaperOps {

//  def createDiff(from: Paper, to: Paper, origin: Vector[String]): PaperDiff = {
//    val modified = to.modified
//    val fromJson = Paper.model2json(from)
//    val toJson = Paper.model2json(to)
//
//    val added = JsonUtil.addedFields(fromJson, toJson).toMap
//    val deleted = JsonUtil.deletedKeys(fromJson, toJson)
//    val jsonDiff = JsonDiff(deleted, added)
//    PaperDiff(modified, jsonDiff, origin)
//  }
//
//  def reduceByInterval(p: Paper, interval: Duration = 5.minutes): Paper = {
//    val diffs = p.diffs.sortWith(_.modified > _.modified)
//    // Dumb check
//    assert(diffs.map(_.modified).size == diffs.map(_.modified).distinct.size, "Diffs must have different modified values")
//    val reducedDiffs = diffs.foldLeft(Vector.empty[PaperDiff]) {
//      (acc, d) =>
//        if (acc.isEmpty || math.abs(acc.last.modified - d.modified) > interval.toMillis) {
//          d +: acc
//        } else {
//          val updatedDiff = PaperDiff.mergeOldToNew(acc.head, d)
//          acc.updated(0, updatedDiff)
//        }
//    }
//    p.copy(diffs = reducedDiffs)
//  }

}
