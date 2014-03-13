package util

import play.api.libs.json._
import play.api.libs.json.JsObject

object JsonDiffUtil {

  def dotPath(p: Seq[String], key: String): String = (p :+ key).mkString(".")

  def mongoModification(old: JsValue, curr: JsValue): JsObject = {
    val d = deletedKeys(old, curr)
    val added = addedFields(old, curr)
    Json.obj(
      "$unset" -> JsObject(
        d.map(key => key -> JsString(""))
      ),
      "$set" -> JsObject(
        added
      )
    )
  }

  def deletedKeys(old: JsValue, curr: JsValue): Vector[String] = {
    deletedKeys(old, curr, Vector.empty, Vector.empty)
  }

  private def deletedKeys(
    old: JsValue,
    curr: JsValue,
    parents: Vector[String],
    acc: Vector[String]
  ): Vector[String] = {
    if (old == curr) {
      Vector.empty
    } else {
      val r = old match {
        case a: JsObject =>
          a.fields.map {
            case (key, b) =>
              if ((curr \ key).isInstanceOf[JsUndefined]) {
                Vector(dotPath(parents, key))
              } else {
                deletedKeys(b, curr \ key, parents :+ key, acc)
              }
          }.flatten.toVector
        case _ =>
          Vector.empty
      }
      r
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
    if (old == curr) {
      Vector.empty
    } else {
      val r = curr match {
        case a: JsObject =>
          a.fields.map {
            case (key, b) =>
              if ((old \ key).isInstanceOf[JsUndefined]) {
                Vector(dotPath(parents, key) -> b)
              } else {
                addedFields(old \ key, b, parents :+ key, acc)
              }
          }.flatten.toVector
        case _ =>
          Vector.empty
      }
      r
    }
  }

  // TODO optimize array diffs


}
