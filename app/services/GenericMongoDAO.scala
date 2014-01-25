package services

import models.{Paper, Jsonable}
import play.api.libs.json.{Json, JsObject}
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.BSONDocument
import play.api.Play.current
import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.ReactiveMongoPlugin
import scala.concurrent.Future

object MongoShop {
  def db = ReactiveMongoPlugin.db
  def elements = db.collection[JSONCollection]("elements")
}

trait MongoDAOTrait {
}

object PaperDAO extends MongoDAOTrait {
  def papers = MongoShop.db.collection[JSONCollection]("papers")
  val jsonable = Paper

  def findById(id: String): Future[Option[JsObject]] =
    papers.find(Json.obj("_id" -> id)).one[JsObject]



}
