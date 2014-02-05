package models

import play.api.libs.json.{Json, JsValue, JsObject}
import play.api.libs.json.Json.JsValueWrapper
import util.Implicits._

trait Jsonable[T] {
  def model2json(m: T): JsObject

  def json2model(j: JsValue): T
}

case class Group(
        id: String,
        title: Option[String],
        x: Option[Int],
        y: Option[Int],
        width: Option[Int],
        height: Option[Int],
        elementIds: Vector[String],
        created: Long,
        lastUpdated: Long
                  ) {

}
object Group extends Jsonable[Group] {
  val id = "_id"
  val title = "title"
  val x = "x"
  val y = "y"
  val width = "width"
  val height = "height"
  val elementIds = "elementIds"
  val created = "created"
  val lastUpdated = "lastUpdated"


  def json2model(j: JsValue): Group = {
    try {
      val p = Group.apply(
        id = j asString Group.id,
        title = j getAsString Group.title,
        x = j getAsInt Group.x,
        y = j getAsInt Group.y,
        width = j getAsInt Group.width,
        height = j getAsInt Group.height,
        elementIds = (j \ Group.elementIds).as[Vector[String]],
        created = j asLong Group.created,
        lastUpdated = j asLong Group.lastUpdated
      )
      p
    } catch {
      case e: Exception =>
        throw e
    }
  }

  def model2json(m: Group): JsObject = {
    val b = Seq.newBuilder[(String, JsValueWrapper)]
    b += Group.id -> m.id
    b += Group.title -> m.title
    m.x.map(b += Group.x -> _)
    m.y.map(b += Group.y -> _)
    m.width.map(b += Group.width -> _)
    m.height.map(b += Group.height -> _)
    b += Group.elementIds -> m.elementIds
    b += Group.created -> m.created
    b += Group.lastUpdated -> m.lastUpdated

    val r = b.result()
    Json.obj(r: _*)
  }
}

case class Paper(
                  id: String,
                  title: String,
                  tags: Vector[String],
                  created: Long,
                  lastUpdated: Long,
                  elements: Vector[Element],
                  groups: Vector[Group]
                  ) {
  def updatedTime() = this.copy(lastUpdated = System.currentTimeMillis())
}

object Paper extends Jsonable[Paper] {
  
  val n = 7

  def createBlank(id: String): Paper = {
    val now = System.currentTimeMillis
    Paper(id, "New Paper", Vector.empty, now, now, Vector.empty, Vector.empty)
  }

  val id = "_id"
  val title = "title"
  val tags = "tags"
  val lastUpdated = "lastUpdated"
  val elements = "elements"
  val created = "created"
  val groups = "groups"

  def model2json(m: Paper): JsObject = {
    val b = Seq.newBuilder[(String, JsValueWrapper)]
    b += Paper.id -> m.id
    b += Paper.title -> m.title
    b += Paper.tags -> m.tags
    b += Paper.elements -> m.elements.map(Element.model2json)
    b += Paper.groups -> m.groups.map(Group.model2json)
    b += Paper.created -> m.created
    b += Paper.lastUpdated -> m.lastUpdated

    val r = b.result()
    Json.obj(r: _*)
  }

  def json2model(j: JsValue): Paper = {
    try {
      val p = Paper.apply(
        id = j asString Paper.id,
        title = j asString Paper.title,
        tags = (j \ Paper.tags).as[Vector[String]],
        created = j asLong Paper.created,
        lastUpdated = j asLong Paper.lastUpdated,
        elements = (j \ Paper.elements).as[Vector[JsObject]].map(Element.json2model),
        groups = (j \ Paper.groups).as[Vector[JsObject]].map(Group.json2model)
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
                    kind: String,
                    data: String,
                    x: Int,
                    y: Int,
                    z: Int,
                    created: Long,
                    lastUpdated: Long
                    ) {

}

object Element extends Jsonable[Element] {

  val n = 8

  val id = "_id"
  val kind = "kind"
  val data = "data"
  val x = "x"
  val y = "y"
  val z = "z"
  val created = "created"
  val lastUpdated = "lastUpdated"

  def model2json(m: Element): JsObject = {
    val b = Seq.newBuilder[(String, JsValueWrapper)]
    b += Element.id -> m.id
    b += Element.kind -> m.kind
    b += Element.data -> m.data
    b += Element.x -> m.x
    b += Element.y -> m.y
    b += Element.z -> m.z
    b += Element.created -> m.created
    b += Element.lastUpdated -> m.lastUpdated
    val r = b.result()
    assert(r.length == Element.n)
    Json.obj(r: _*)
  }

  def json2model(j: JsValue): Element = {
    try {
      val e = Element.apply(
        id = j asString Element.id,
        kind = j asString Element.kind,
        data = j asString Element.data,
        x = j asInt Element.x,
        y = j asInt Element.y,
        z = j asInt Element.z,
        created = j asLong Element.created,
        lastUpdated = j asLong Element.lastUpdated
      )
      e
    } catch {
      case e: Exception =>
        throw e
    }
  }
}
