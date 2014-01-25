package services

import models.{Paper, Jsonable}
import play.api.libs.json.{Json, JsObject}
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.BSONDocument
import play.api.Play.current
import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.ReactiveMongoPlugin
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._
import reactivemongo.core.commands.LastError

object MongoShop {
  def db = ReactiveMongoPlugin.db
  def elements = db.collection[JSONCollection]("elements")
  def papers = db.collection[JSONCollection]("papers")
}

trait MongoDAOTrait[Model] {

  def coll: JSONCollection

  def jsonable: Jsonable[Model]

  def save(m: Model, ow: Boolean = false): Future[LastError] = {
    val dbo = jsonable.model2json(m)
    try {
      if (ow) coll.save(dbo)
      else coll.insert(dbo)
    } catch {
      case e: Exception =>
        throw e
    }
  }
}

object PaperDAO extends MongoDAOTrait[Paper] {
  def coll = MongoShop.db.collection[JSONCollection]("papers")

  val jsonable = Paper

  def findById(id: String): Future[Option[JsObject]] =
    coll.find(Json.obj("_id" -> id)).one[JsObject]

  def findModel(q: JsObject, limit: Int): Future[Vector[Paper]] = {
    coll.find(q).cursor[JsObject].collect[Vector](25).map(_.map(jsonable.json2model))
  }



}
