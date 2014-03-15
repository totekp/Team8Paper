package util

import play.api.libs.json._
import play.api.libs.json.JsObject

object JsonDiffUtil {

  def dotPath(p: Seq[String], key: String): String = (p :+ key).mkString(".")

  def deletedKeys(old: JsValue, curr: JsValue): Vector[String] = {
    deletedKeys(old, curr, Vector.empty, Vector.empty)
  }

  private def deletedKeys(
    old: JsValue,
    curr: JsValue,
    parents: Vector[String],
    acc: Vector[String]
  ): Vector[String] = {
    (old, curr) match {
      case (old: JsObject, curr: JsObject) =>
        old.fields.map {
          case (key, oldSubDoc: JsObject) =>
            deletedKeys(oldSubDoc, curr \ key, parents :+ key, acc)

          case (key, oldSubDoc) =>
            if ((curr \ key).isInstanceOf[JsUndefined]) {
              Vector(dotPath(parents, key))
            } else {
              Vector.empty
            }
        }.flatten.toVector

      case _ =>
        Vector.empty
    }
  }

  object mongo {

    def modifications(old: JsValue, curr: JsValue): JsObject = {
      val d = deletedKeys(old, curr)
      val added = addedFields(old, curr)
      createModQuery(d, added)
    }

    def merge(oldMod: JsValue, currMod: JsValue): JsObject = {
      val currDeleted = (currMod \ "$unset").as[JsObject].keys
      val currAdded = (currMod \ "$set").as[JsObject].keys
      assert(currDeleted.intersect(currAdded).isEmpty, "no common keys in one modification")

      val oldDeletedAfterCurr = (oldMod \ "$unset").as[JsObject].keys diff currAdded
      val oldAddedAfterCurr = (oldMod \ "$set").as[JsObject].keys diff currDeleted
      assert(oldDeletedAfterCurr.intersect(oldAddedAfterCurr).isEmpty, "no common keys in one modification")

      assert(currDeleted.intersect(oldDeletedAfterCurr).isEmpty, "Key cannot be in both deleted")
      assert(currAdded.intersect(oldAddedAfterCurr).isEmpty, "Key cannot be in both added")

      val finalDelete = (currDeleted ++ oldDeletedAfterCurr).toSeq
      val finalAdd = currAdded.toSeq.map {
        key =>
          key -> currMod \ "$set" \ key
      } ++ oldAddedAfterCurr.toSeq.map {
        key =>
          key -> oldMod \ "$set" \ key
      }
      createModQuery(finalDelete, finalAdd)
    }

    def mergeOldToNew(oldToNew: Seq[JsObject]) = {
      oldToNew.reduce(merge)
    }

    def createModQuery(toDelete: Seq[String], toAdd: Seq[(String, JsValue)]): JsObject = {
      Json.obj(
        "$unset" -> JsObject(
          toDelete.map(key => key -> JsString(""))
        ),
        "$set" -> JsObject(
          toAdd
        )
      )
    }

  }

  def addedFields(old: JsValue, curr: JsValue): Vector[(String, JsValue)] = {
    addedFields(old, curr, Vector.empty, Vector.empty)
  }

  private def addedFields(
    old: JsValue,
    curr: JsValue,
    parents: Vector[String],
    acc: Vector[(String, JsValue)]
  ): Vector[(String, JsValue)] = {
    curr match {
      case curr: JsObject =>
        curr.fields.map {
          case (key, currSubDoc: JsObject) if (old \ key).isInstanceOf[JsObject] =>
            addedFields(old \ key, currSubDoc, parents :+ key, acc)

          case (key, currSubDoc) =>
            if (currSubDoc != (old \ key)) {
              Vector(dotPath(parents, key) -> currSubDoc)
            } else {
              Vector.empty
            }
        }.flatten.toVector

      case _ =>
        Vector.empty
    }
  }

  // TODO optimize array diffs


}
