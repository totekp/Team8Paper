package util

import play.api.mvc.RequestHeader
import models.Paper
import util.Implicits._

object T8Logger {

  def originDetails(rh: RequestHeader): Vector[String] = {
    Vector(
      rh.remoteAddress,
      rh.session.data.toVector.map(t => s"${t._1}=${t._2}").mkString("&")
    )
  }

  def getUpdateMessage(prev: Paper, curr: Paper): Option[String] = {
    val sb = StringBuilder.newBuilder
    val addedTags = curr.tags -- prev.tags
    if (addedTags.nonEmpty)
      sb.append(s"Added tags: ${addedTags.toVector.sorted.mkString(",")}\n")
    val deletedTags = prev.tags -- curr.tags
    if (deletedTags.nonEmpty)
      sb.append(s"Deleted tags: ${deletedTags.toVector.sorted.mkString(",")}\n")
    
    if (prev.title != curr.title)
      sb.append(s"Updated title to: ${curr.title}\n")

    if (prev.elements != curr.elements)
      sb.append(s"Updated elements\n")

    if (prev.groups != curr.groups)
      sb.append(s"Updated groups\n")

    sb.result().toOpt
  }

}
