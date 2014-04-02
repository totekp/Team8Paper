package util

import services.{MongoShop, PaperDAO}
import reactivemongo.core.commands._
import reactivemongo.bson.{BSONArray, BSONInteger, BSONDocument}
import models.Paper
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.Future
import reactivemongo.api._
import reactivemongo.bson._
import play.api.Logger


object Aggregation {

/** {
        "result" : [
                {
                        "_id" : "Bad",
                        "count" : 1
                },
                {
                        "_id" : "abc",
                        "count" : 5
                },
                {
                        "_id" : "test",
                        "count" : 23
                },
                {
                        "_id" : "kefu",
                        "count" : 5
                },
                {
                        "_id" : "papier",
                        "count" : 2
                }
        ],
        "ok" : 1
}
 */
  def tagCloud(username: Option[String]): Future[Map[String, Int]] = {
    val mat = {
      username match {
        case Some(username) => Match(BSONDocument(Paper.username -> username))
        case None => Match(BSONDocument(Paper.username -> BSONDocument("$exists" -> false)))
      }
    }
    val project = Project(Paper.tags -> BSONInteger(1), "_id" -> BSONInteger(0))
    val unwind = Unwind(Paper.tags)
    val group = GroupField(Paper.tags)("count" -> SumValue(1))

    val r = Aggregate(PaperDAO.coll.name, Seq(mat, project, unwind, group))
    MongoShop.db.command(r).map {
      b =>
        val result = b.toVector
        result.map {
          tb: BSONDocument =>
            val tagName = tb.getAs[String]("_id").get
            val count = tb.getAs[Int]("count").get
            tagName -> count
        }.toMap
    }
  }

}
