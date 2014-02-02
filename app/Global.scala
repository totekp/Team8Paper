
import play.filters.gzip.GzipFilter
import play.api._
import play.api.mvc._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._

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

}
