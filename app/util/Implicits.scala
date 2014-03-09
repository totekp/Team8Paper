package util

import play.api.libs.json._
import scala.util.Try

object Implicits {
    implicit class JsonHelper(j: JsValue) {
      def asBoolean(key: String) = (j \ key).as[Boolean]
      def asString(key: String) = Try((j \ key).as[String])
        .getOrElse(throw new Exception(s"key $key not found in json object"))
      def getAsString(key: String) = (j \ key).asOpt[String]
      def getAsLong(key: String) = (j \ key).asOpt[Long]
      def asInt(key: String) = (j \ key).as[Int]
      def getAsInt(key: String) = (j \ key).asOpt[Int]
      def getAsObject(key: String) = (j \ key).asOpt[JsObject]
      def asObject(key: String) = (j \ key).as[JsObject]
      def asLong(key: String) = Try((j \ key).as[Long])
        .getOrElse(throw new Exception(s"key $key not found in json object"))
    }
}
