package util

import java.util.UUID
import com.sun.corba.se.spi.ior.ObjectId
import reactivemongo.bson.BSONObjectID

object Generator {
  def uuid() = UUID.randomUUID().toString.replaceAll("-", "")
  def oid(): String =  BSONObjectID.generate.toString
}