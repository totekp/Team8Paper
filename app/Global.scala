
import models.Paper
import play.filters.gzip.GzipFilter
import play.api._
import play.api.mvc._
import reactivemongo.api.indexes.{IndexType, Index}
import scala.concurrent.{Await, Future}
import play.api.libs.concurrent.Execution.Implicits._
import services.PaperDAO
import scala.concurrent.duration._

object Global extends WithFilters(new GzipFilter()) with GlobalSettings {


  override def onHandlerNotFound(request: RequestHeader): Future[SimpleResult] = {
    if (request.path.endsWith("/")) {
      Future {
        val uri = request.path.reverse.dropWhile(_ == '/').reverse
        Results.MovedPermanently(uri)
      }
    } else {
      super.onHandlerNotFound(request)
    }
  }

  override def onStart(app: Application) {
    val f1 = PaperDAO.coll.indexesManager.ensure(Index(Seq(Paper.title -> IndexType.Descending)))
    val f2 = PaperDAO.coll.indexesManager.ensure(Index(Seq(Paper.tags -> IndexType.Descending)))
    val r = List(f1, f2)

    assert(Await.result(Future.sequence(r), 30.seconds).forall(_ == true), "Indices cannot be ensured")
  }


}
