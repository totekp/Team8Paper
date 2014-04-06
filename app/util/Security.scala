package util

import play.api.mvc.RequestHeader

object Security {

  def originDetails(rh: RequestHeader): Vector[String] = {
    Vector(
      rh.remoteAddress,
      rh.session.data.toVector.map(t => s"${t._1}=${t._2}").mkString("&")
    )
  }

}
