package services

import models.{JsonDiff, PaperDiff, Paper}
import util.JsonUtil

object PaperOps {

  def createDiff(from: Paper, to: Paper, origin: Vector[String]): PaperDiff = {
    val modified = to.modified
    val fromJson = Paper.model2json(from)
    val toJson = Paper.model2json(to)

    val added = JsonUtil.addedFields(fromJson, toJson).toMap
    val deleted = JsonUtil.deletedKeys(fromJson, toJson)
    val jsonDiff = JsonDiff(deleted, added)
    PaperDiff(modified, jsonDiff, origin)
  }

}
