
import controllers.Users
import models.{JsonResult, Paper}
import play.api.http.Status
import play.api.libs.json.Json
import play.filters.gzip.GzipFilter
import play.api._
import play.api.mvc._
import reactivemongo.api.indexes.{IndexType, Index}
import scala.concurrent.{Await, Future}
import play.api.libs.concurrent.Execution.Implicits._
import services.PaperDAO
import scala.concurrent.duration._
import play.api.Play.current

object Global extends WithFilters(HttpsFilter, Api1Filter, new GzipFilter()) with GlobalSettings {


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
    try {
      val f1 = PaperDAO.coll.indexesManager.ensure(Index(Seq(Paper.title -> IndexType.Descending), unique = false))
      val f2 = PaperDAO.coll.indexesManager.ensure(Index(Seq(Paper.tags -> IndexType.Descending), unique = false))
      val f3 = PaperDAO.coll.indexesManager.ensure(Index(Seq(Paper.created -> IndexType.Descending), unique = false))
      val f4 = PaperDAO.coll.indexesManager.ensure(Index(Seq(Paper.modified -> IndexType.Descending), unique = false))

      Await.result(f1, Duration.Inf)
      Await.result(f2, Duration.Inf)
      Await.result(f3, Duration.Inf)
      Await.result(f4, Duration.Inf)
    } catch {
      case e: Exception =>
        Logger.error(e.getMessage)
        Logger.error(e.getStackTraceString)
    }
  }
}

object HttpsFilter extends Filter {
  def apply(nextFilter: (RequestHeader) => Future[SimpleResult])
           (rh: RequestHeader): Future[SimpleResult] = {

    nextFilter(rh).map {
      result =>
        if (Play.isProd && !rh.headers.get("x-forwarded-proto").getOrElse("").contains("https")) {
          Results.MovedPermanently("https://" + rh.host + rh.uri)
        } else {
          result
        }
    }
  }
}

object Api1Filter extends Filter {
  def apply(nextFilter: (RequestHeader) => Future[SimpleResult])
           (rh: RequestHeader): Future[SimpleResult] = {

    nextFilter(rh).map {
      result =>
        if (result.header.status == Status.NOT_FOUND && rh.path.startsWith("/api1")) {
          JsonResult.error(s"Path ${rh.path} is not found. Refer to routes file or docs.")
        } else {
          result
        }
    }
  }
}