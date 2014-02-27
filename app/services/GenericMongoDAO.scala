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

  def removeById(id: String): Future[LastError] = {
    coll.remove(Json.obj("_id" -> id))
  }

  def remove(q: JsObject): Future[LastError] = coll.remove(q)
}

object PaperDAO extends MongoDAOTrait[Paper] {
  def coll = MongoShop.db.collection[JSONCollection]("papers")

  val jsonable = Paper

  def findById(id: String): Future[Option[JsObject]] =
    coll.find(Json.obj("_id" -> id)).one[JsObject]

  def findByIdModel(id: String): Future[Option[Paper]] =
    findById(id).map(_.map(jsonable.json2model))

  def find(q: JsObject, s: JsObject, limit: Int = Int.MaxValue): Future[Vector[JsObject]] = {
    coll.find(q).sort(s).cursor[JsObject].collect[Vector](limit)
  }

  def findOne(q: JsObject): Future[Option[JsObject]] = {
    coll.find(q).one[JsObject]
  }


  def findOneModel(q: JsObject): Future[Option[Paper]] = {
    findOne(q).map(_.map(jsonable.json2model))
  }

  def findModel(q: JsObject, s: JsObject, limit: Int = Int.MaxValue): Future[Vector[Paper]] = {
    find(q, s, limit).map(_.collect{
      case j: JsObject => jsonable.json2model(j)
    })
  }



}
