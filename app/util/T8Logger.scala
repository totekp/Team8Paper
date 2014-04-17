package util

import play.api.mvc.RequestHeader
import models.Paper
import util.Implicits._
import play.api.Logger

object T8Logger {

  def originDetails(rh: RequestHeader): Vector[String] = {
    Vector(
      rh.remoteAddress,
      rh.session.data.toVector.map(t => s"${t._1}=${t._2}").mkString("&")
    )
  }

  def getUpdates(prev: Paper, curr: Paper): Vector[String] = {
    var buffer = List[String]()
    val addedTags = curr.tags -- prev.tags
    if (addedTags.nonEmpty)
      buffer ::= s"Added tags: ${addedTags.toVector.sorted.mkString(",")}"
    val deletedTags = prev.tags -- curr.tags
    if (deletedTags.nonEmpty)
      buffer ::= (s"Deleted tags: ${deletedTags.toVector.sorted.mkString(",")}\n")
    
    if (prev.title != curr.title)
      buffer ::= (s"Updated title to: ${curr.title}\n")

    if (prev.elements != curr.elements)
      buffer ::= (s"Updated elements\n")

    if (prev.groups != curr.groups)
      buffer ::= (s"Updated groups\n")

    buffer.toVector
  }

}
