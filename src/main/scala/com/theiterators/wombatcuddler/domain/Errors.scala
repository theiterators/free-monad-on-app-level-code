package com.theiterators.wombatcuddler.domain

sealed abstract class Error
case object IncorrectPIN  extends Error
case object EmailNotFound extends Error

sealed abstract class RequestError extends Error
case class Empty(field: String)    extends RequestError
case object EmailFormatError extends RequestError