package models

import util.Implicits._
import play.api.libs.json.{JsValue, Json, JsObject}
import play.api.libs.json.Json.JsValueWrapper
import play.api.Logger
import util.{Generator, PasswordCrypto}

case class User(
  _id: String,
  username: String,
  created: Long,
  lastUpdated: Long,
  hash: String,
  salt: String,
  userPrivs: Option[String]
)

object User extends Jsonable[User] {
  val _id = "_id"
  val username = "username"
  val created = "created"
  val lastUpdated = "lastUpdated"
  val hash = "hash"
  val salt = "salt"
  val userPrivs = "userPrivs"

  def create(username: String, password: String, privs: Option[String]): User = {
    val salt =  PasswordCrypto.makeSalt(25)
    val hash = PasswordCrypto.passwordHash(salt, password)
    val now = System.currentTimeMillis()
    val _id = Generator.oid()
    val u = User(_id, username, now, now, hash, salt, privs)
    u
  }

  def updatePassword(user: User, oldPassword: String, newPassword: String): User = {
    // TODO move out password check to controller
    if (PasswordCrypto.checkPassword(user.salt, oldPassword, user.hash)) {
      val newSalt = PasswordCrypto.makeSalt(25)
      val newHash = PasswordCrypto.passwordHash(salt, newPassword)
      user.copy(
        lastUpdated = System.currentTimeMillis(),
        hash = newHash,
        salt = newSalt
      )
    } else {
      throw new Exception("Old password incorrect")
    }
  }

  def model2json(m: User): JsObject = {
    val b = Seq.newBuilder[(String, JsValueWrapper)]
    b += User._id -> m._id
    b += User.username -> m.username
    b += User.created -> m.created
    b += User.lastUpdated -> m.lastUpdated
    b += User.hash -> m.hash
    b += User.salt -> m.salt
    m.userPrivs.map(b += User.userPrivs -> _)
    val r = b.result()
    Json.obj(r: _*)
  }

  def json2model(j: JsValue): User = {
    try {
      val p = User.apply(
        _id = j asString User._id,
        username = j asString User.username,
        created = j asLong User.created,
        lastUpdated = j asLong User.lastUpdated,
        hash = j asString User.hash,
        salt = j asString User.salt,
        userPrivs = j getAsString User.userPrivs
      )
      p
    } catch {
      case e: Exception =>
        Logger.error(e.getStackTraceString)
        throw e
    }
  }

}