package util

import play.api.libs.json._

object Implicits {
    implicit class JsonHelper(j: JsValue) {
      def asBoolean(key: String) = (j \ key).as[Boolean]
      def asString(key: String) = (j \ key).as[String]
      def getAsString(key: String) = (j \ key).asOpt[String]
      def getAsLong(key: String) = (j \ key).asOpt[Long]
      def asInt(key: String) = (j \ key).as[Int]
      def getAsInt(key: String) = (j \ key).asOpt[Int]
      def getAsObject(key: String) = (j \ key).asOpt[JsObject]
      def asObject(key: String) = (j \ key).as[JsObject]
      def asLong(key: String) = (j \ key).as[Long]
    }
}
