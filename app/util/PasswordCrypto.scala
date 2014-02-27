package util

import scala.util.Random
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

trait PasswordService {
  /**
   * Custom hash function
   */
  protected def passwordHash(salt: String, password: String, f: (String, String) => String): String

  /**
   * Creates a salt string
   */
  protected def makeSalt(size: Int): String

  /**
   * Checks password in input with salted password
   */
  final def checkPassword(salt: String, password: String, hash: String, f: (String, String) => String = _ + _): Boolean = {
    passwordHash(salt, password, f) == hash
  }

}

object PasswordCrypto extends PasswordService {

  /**
   * Gets a character for salt
   */
  protected def nextChar(): Char = Random.nextPrintableChar()

  /**
   * Creates a salt string
   */
  def makeSalt(size: Int = 10): String = {
    (0 until size).map(_ => nextChar()).mkString
  }

  /**
   * Custom hash function
   */
  def passwordHash(salt: String, password: String, combine: (String, String) => String = _ + _): String = {
    val input = combine(salt, password)
    "v1@"+sha256(sha256(input) + salt.reverse + input + input.takeRight(5))
    // TODO potentially embed version, use secret, and slow functions
  }

  protected lazy val digest: MessageDigest = {
    try {
      MessageDigest.getInstance("SHA-256")
    } catch {
      case e: NoSuchAlgorithmException =>
        throw e
      case e: Throwable =>
        throw e
    }
  }

  protected def sha256(input: String): String = {
    try {
      val bytes = digest.digest(input.getBytes)
      digest.reset()
      BigInt(1, bytes).toString(16)
    } catch {
      case e: Exception =>
        throw e
      case t: Throwable =>
        throw t
    }
  }
}