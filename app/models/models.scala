package models

import play.api.libs.json.{JsArray, Json, JsValue, JsObject}
import play.api.libs.json.Json.JsValueWrapper
import util.Implicits._
import play.api.Logger
import play.api.mvc.{Request, AnyContent}
import scala.concurrent.duration._

trait Jsonable[T] {
  def model2json(m: T): JsObject

  def json2model(j: JsValue): T

  def jsonString2model(s: String): T = {
    val j = Json.parse(s)
    json2model(j)
  }

  def model2JsonString(m: T): String = {
    val j = model2json(m)
    Json.stringify(j)
  }

  def jsonString2json(s: String): JsObject = {
    Json.parse(s).as[JsObject]
  }

  def json2jsonString(j: JsValue): String = {
    Json.stringify(j)
  }
}

case class Group(
        _id: String,
        title: Option[String],
        x: Option[Int],
        y: Option[Int],
        width: Option[Int],
        height: Option[Int],
        elementIds: Vector[String],
        created: Long,
        modified: Long
                  ) {

}
object Group extends Jsonable[Group] {
  val _id = "_id"
  val title = "title"
  val x = "x"
  val y = "y"
  val width = "width"
  val height = "height"
  val elementIds = "elementIds"
  val created = "created"
  val modified = "modified"


  def json2model(j: JsValue): Group = {
    try {
      val p = Group.apply(
        _id = j asString Group._id,
        title = j getAsString Group.title,
        x = j getAsInt Group.x,
        y = j getAsInt Group.y,
        width = j getAsInt Group.width,
        height = j getAsInt Group.height,
        elementIds = (j \ Group.elementIds).as[Vector[String]],
        created = j asLong Group.created,
        modified = j asLong Group.modified
      )
      p
    } catch {
      case e: Exception =>
        throw e
    }
  }

  def model2json(m: Group): JsObject = {
    val b = Seq.newBuilder[(String, JsValueWrapper)]
    b += Group._id -> m._id
    b += Group.title -> m.title
    m.x.map(b += Group.x -> _)
    m.y.map(b += Group.y -> _)
    m.width.map(b += Group.width -> _)
    m.height.map(b += Group.height -> _)
    b += Group.elementIds -> m.elementIds
    b += Group.created -> m.created
    b += Group.modified -> m.modified

    val r = b.result()
    Json.obj(r: _*)
  }
}

case class Paper(
  _id: String,
  title: String,
  tags: Set[String],
  created: Long,
  modified: Long,
  elements: Set[Element],
  groups: Set[Group],
  username: Option[String],
  permissions: Option[String] = None,
  diffs: Vector[SimpleDiff]
) {
  def elementIds = elements.map(_._id)
  def groupIds = groups.map(_._id)
  def updatedTime() = this.copy(modified = System.currentTimeMillis())
  def hasUsername = username.isDefined

  def prependDiff(message: String, req: Request[AnyContent]): Paper = {
    val newDiff = SimpleDiff.create(message, Vector(req.remoteAddress), this.modified)
    this.copy(diffs = SimpleDiff.combine(5.minutes, newDiff +: diffs))
  }

//  def diffWithNew(newer: Paper, origin: Vector[String]): PaperDiff = {
//    assert(newer.modified > this.modified, "Older paper's modified must be less than this paper's modified time")
//    newer.diffWithOlder(this, origin)
//  }
//
//  def addDiff(older: Paper, origin: Vector[String]): Paper = {
//    val diff = this.diffWithOlder(older, origin)
//    this.copy(
//      diffs = diff +: this.diffs
//    )
//  }
//
//  def diffWithOlder(old: Paper, origin: Vector[String]): PaperDiff = {
//    assert(old.modified < this.modified, "Older paper's modified must be less than this paper's modified time")
//    val newTitle = {
//      if (this.title != old.title)
//        Some(this.title)
//      else
//        None
//    }
//    val diffTags = DiffSet.create(this.tags -- old.tags, old.tags -- this.tags)
//    val diffGroups = DiffSet.create(
//      (this.groups -- old.groups).map(Group.model2JsonString),
//      (old.groups -- this.groups).map(Group.model2JsonString)
//    )
//    val diffElements = DiffSet.create(
//      (this.elements -- old.elements).map(Element.model2JsonString),
//      (old.elements -- this.elements).map(Element.model2JsonString)
//    )
//    PaperDiff(
//      modified = this.modified,
//      diff = JsonDiff.empty, // TODO
//      origin = origin
//    )
//  }
}

object Paper extends Jsonable[Paper] {
  
  def createBlank(id: String, username: Option[String],
                  permissions: Option[String] = None): Paper = {
    val now = System.currentTimeMillis
    Paper(
      id,
      "Untitled Paper",
      Set.empty,
      now,
      now,
      Set.empty,
      Set.empty,
      username,
      permissions,
      Vector.empty
    )
  }

  val _id = "_id"
  val title = "title"
  val tags = "tags"
  val modified = "modified"
  val elements = "elements"
  val created = "created"
  val groups = "groups"
  val username = "username"
  val permissions = "permissions"
  val diffs = "diffs"

  def model2json(m: Paper): JsObject = {
    val b = Seq.newBuilder[(String, JsValueWrapper)]
    b += Paper._id -> m._id
    b += Paper.title -> m.title
    b += Paper.tags -> m.tags
    b += Paper.elements -> m.elements.map(Element.model2json)
    b += Paper.groups -> m.groups.map(Group.model2json)
    b += Paper.created -> m.created
    b += Paper.modified -> m.modified
    m.username.map(b += Paper.username -> _)
    m.permissions.map(b += Paper.permissions -> _)
    b += Paper.diffs -> m.diffs

    val r = b.result()
    Json.obj(r: _*)
  }

  def json2model(j: JsValue): Paper = {
    try {
      val p = Paper.apply(
        _id = j asString Paper._id,
        title = j asString Paper.title,
        tags = (j \ Paper.tags).as[Set[String]],
        created = j asLong Paper.created,
        modified = j asLong Paper.modified,
        elements = (j \ Paper.elements).as[Set[JsObject]].map(Element.json2model),
        groups = (j \ Paper.groups).as[Set[JsObject]].map(Group.json2model),
        username = j getAsString Paper.username,
        permissions = j getAsString Paper.permissions,
        diffs = (j \ Paper.diffs).as[JsArray].value.toVector.map(_.as[SimpleDiff])
      )
      p
    } catch {
      case e: Exception =>
        Logger.error(e.getStackTraceString)
        throw e
    }
  }
}

/**
kind -> video, image, text,
  */
case class Element(
                    _id: String,
                    kind: String,
                    data: String,
                    x: Int,
                    y: Int,
                    z: Int,
                    width: Int,
                    height: Int,
                    created: Long,
                    modified: Long
                    ) {

}

object Element extends Jsonable[Element] {

  val n = 10

  val _id = "_id"
  val kind = "kind"
  val data = "data"
  val x = "x"
  val y = "y"
  val z = "z"
  val width = "width"
  val height = "height"
  val created = "created"
  val modified = "modified"

  def model2json(m: Element): JsObject = {
    val b = Seq.newBuilder[(String, JsValueWrapper)]
    b += Element._id -> m._id
    b += Element.kind -> m.kind
    b += Element.data -> m.data
    b += Element.x -> m.x
    b += Element.y -> m.y
    b += Element.z -> m.z
    b += Element.width -> m.width
    b += Element.height -> m.height
    b += Element.created -> m.created
    b += Element.modified -> m.modified
    val r = b.result()
    Json.obj(r: _*)
  }

  def json2model(j: JsValue): Element = {
    try {
      val e = Element.apply(
        _id = j asString Element._id,
        kind = j asString Element.kind,
        data = j asString Element.data,
        x = j asInt Element.x,
        y = j asInt Element.y,
        z = j asInt Element.z,
        width = j asInt Element.width,
        height = j asInt Element.height,
        created = j asLong Element.created,
        modified = j asLong Element.modified
      )
      e
    } catch {
      case e: Exception =>
        throw e
    }
  }
}