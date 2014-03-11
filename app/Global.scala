
import controllers.Users
import models.Paper
import play.filters.gzip.GzipFilter
import play.api._
import play.api.mvc._
import reactivemongo.api.indexes.{IndexType, Index}
import scala.concurrent.{Await, Future}
import play.api.libs.concurrent.Execution.Implicits._
import services.PaperDAO
import scala.concurrent.duration._
import play.api.Play.current

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

  override def onRouteRequest(request: RequestHeader): Option[Handler] = {
    if (Play.isProd && !request.headers.get("x-forwarded-proto").getOrElse("").contains("https")) {
      Some(Users.secure)
    } else {
      super.onRouteRequest(request)
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
