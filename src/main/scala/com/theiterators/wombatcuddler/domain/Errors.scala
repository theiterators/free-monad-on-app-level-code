package com.theiterators.wombatcuddler.domain

sealed abstract class Error {
  val errorCode: String = this.getClass.getSimpleName.stripSuffix("$")
}
case object IncorrectPIN   extends Error
case object EmailNotFound  extends Error
case object DuplicateEmail extends Error

sealed abstract class RequestError extends Error
case class Empty(field: String) extends RequestError {
  override val errorCode: String = s"$field:empty"
}
case object EmailFormatError extends RequestError
