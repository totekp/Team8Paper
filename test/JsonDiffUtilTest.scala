
import org.scalatest.FunSuite
import play.api.libs.json.{JsNumber, JsString, Json}
import util.JsonUtil

class JsonDiffUtilTest extends FunSuite {

  val a = Json.obj(
    "a" -> 123,
    "b" -> Json.obj(
      "a" -> 123,
      "b" -> 123
    ),
    "d" -> 111,
    "e" -> Json.obj("a" -> "a")
  )

  val b = Json.obj(
    "b" -> Json.obj(
      "b" -> 123
    ),
    "c" -> 123,
    "d" -> 123,
    "e" -> 123
  )

  test("find deleted keys") {
    assert(JsonUtil.deletedKeys(a, b).toSet
      == Set("a", "b.a"))
  }

  test("find added fields") {
    assert(JsonUtil.addedFields(a, b).toSet
      == Set("c" -> JsNumber(123), "d" -> JsNumber(123), "e" -> JsNumber(123)))
    assert(JsonUtil.addedFields(b, a).toSet
      == Set(
      "a" -> JsNumber(123),
      "b.a" -> JsNumber(123),
      "d" -> JsNumber(111),
      "e"-> Json.obj("a" -> "a")
    ))
  }

  test("get mongo modification query") {
    assert(JsonUtil.mongo.modifications(a, b)
      ==
      Json.obj(
        "$unset" -> Json.obj("a" -> "", "b.a" -> ""),
        "$set" -> Json.obj("c" -> 123, "d" -> 123, "e" -> 123)
      )
    )
    assert(JsonUtil.mongo.modifications(b, a)
      ==
      Json.obj(
        "$unset" -> Json.obj("c" -> ""),
        "$set" -> Json.obj("a" -> 123, "b.a" -> 123, "d" -> 111, "e" -> Json.obj("a" -> "a"))
      )
    )
  }

  test("merge old and curr") {
    val a2b = JsonUtil.mongo.modifications(a, b)
    val b2a = JsonUtil.mongo.modifications(b, a)
    val r = JsonUtil.mongo.merge(a2b, b2a)
    assert(a2b == Json.parse("""{"$unset":{"a":"","b.a":""},"$set":{"c":123,"d":123,"e":123}}"""))
    assert(b2a == Json.parse("""{"$unset":{"c":""},"$set":{"a":123,"b.a":123,"d":111,"e":{"a":"a"}}}"""))

    assert(r == Json.parse("""{"$unset":{"c":""},"$set":{"a":123,"b.a":123,"d":111,"e":{"a":"a"}}}"""))

    assert(JsonUtil.mongo.mergeOldToNew(Seq(a2b, b2a)) == r)
  }


}