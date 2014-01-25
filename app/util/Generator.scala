package util

import java.util.UUID

object Generator {
  def uuid() = UUID.randomUUID().toString.replaceAll("-", "")
  def oid(): String =  ObjectId
}