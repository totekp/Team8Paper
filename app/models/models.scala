package models

import play.api.libs.json.{Json, JsValue, JsObject}
import play.api.libs.json.Json.JsValueWrapper
import util.Implicits._

trait Jsonable[T] {
  def model2json(m: T): JsObject

  def json2model(j: JsValue): T
}

case class Paper(
                  id: String,
                  title: String,
                  tags: Vector[String],
                  lastupdated: Long
                  ) {
}

object Paper extends Jsonable[Paper] {

  def createBlank(id: String): Paper = {
    val now = System.currentTimeMillis
    Paper(id, "New Paper", Vector.empty, now)
  }

  val id = "_id"
  val title = "title"
  val tags = "tags"
  val lastupdated = "lastupdated"

  def model2json(m: Paper): JsObject = {
    val b = Seq.newBuilder[(String, JsValueWrapper)]
    b += Paper.id -> m.id
    b += Paper.title -> m.title
    b += Paper.tags -> m.tags
    b += Paper.lastupdated -> m.lastupdated
    Json.obj(b.result(): _*)
  }

  def json2model(j: JsValue): Paper = {
    try {
      val p = Paper.apply(
        id = j asString Paper.id,
        title = j asString Paper.title,
        tags = (j \ Paper.tags).as[Vector[String]],
        lastupdated = j asLong Paper.lastupdated
      )
      p
    } catch {
      case e: Exception =>
        throw e
    }
  }
}

/**
kind -> video, image, text,
  */
case class Element(
                    id: String,
                    paperid: String,
                    kind: String,
                    data: String,
                    x: Int,
                    y: Int,
                    z: Int
                    ) {

}

object Element extends Jsonable[Element] {
  val id = "_id"
  val paperid = "paperid"
  val kind = "kind"
  val data = "data"
  val x = "x"
  val y = "y"
  val z = "z"

  def model2json(m: Element): JsObject = {
    val b = Seq.newBuilder[(String, JsValueWrapper)]
    b += Element.id -> m.id
    b += Element.paperid -> m.paperid
    b += Element.kind -> m.kind
    b += Element.data -> m.data
    b += Element.x -> m.x
    b += Element.y -> m.y
    b += Element.z -> m.z
    Json.obj(b.result(): _*)
  }

  def json2model(j: JsValue): Element = {
    try {
      val e = Element.apply(
        id = j asString Element.id,
        paperid = j asString Element.paperid,
        kind = j asString Element.kind,
        data = j asString Element.data,
        x = j asInt Element.x,
        y = j asInt Element.y,
        z = j asInt Element.z
      )
      e
    } catch {
      case e: Exception =>
        throw e
    }
  }
}
