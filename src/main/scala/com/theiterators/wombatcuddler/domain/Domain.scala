package com.theiterators.wombatcuddler.domain

import scala.util.Random

case class Email(value: String) extends AnyVal
object Email {
  object Valid {
    def `addressHas@`(address: String): Boolean = {
      val indexOfAt = address.trim.indexOf('@')
      indexOfAt > 0 && indexOfAt < address.length - 1
    }

    def unapply(arg: Email): Boolean = arg match {
      case Email(address) if `addressHas@`(address) => true
      case _                                        => false
    }
  }
}

case class FullName(value: String) extends AnyVal
case class CV(value: String)       extends AnyVal
case class Letter(value: String)   extends AnyVal

case class PIN(value: String) extends AnyVal
object PIN {
  private val PIN_LENGTH = 8
  def generate: PIN = PIN(Random.alphanumeric.take(PIN_LENGTH).mkString.toUpperCase)
}
