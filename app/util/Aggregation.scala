package util

import services.{MongoShop, PaperDAO}
import reactivemongo.core.commands._
import reactivemongo.bson.{BSONInteger, BSONArray, BSONDocument}
import models.Paper
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.Future


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
  def tagCloud: Future[Map[String, Int]] = {
    val project = Project(Paper.tags -> BSONInteger(1), "_id" -> BSONInteger(0))
    val unwind = Unwind(Paper.tags)
    val group = GroupField(Paper.tags)("count" -> SumValue(1))

    val r = Aggregate(PaperDAO.coll.name, Seq(project, unwind, group))
    MongoShop.db.command(r).map {
      b =>
        val result = b.head.get("result").get.as[Seq[BSONDocument]]
        result.map {
          tb =>
            val tagName = tb.get("_id").get.as[String]
            val count = tb.get("count").get.as[Int]
            tagName -> count
        }.toMap
    }
  }

}
