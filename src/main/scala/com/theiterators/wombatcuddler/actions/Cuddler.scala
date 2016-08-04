package com.theiterators.wombatcuddler.actions

import cats.data.Xor.{Left, Right}
import cats.data.{Xor, XorT}
import cats.free.Free
import cats.std.option._
import cats.syntax.traverse._
import com.theiterators.wombatcuddler.domain._

object Cuddler {
  sealed trait Action[R]
  case class SaveApplication(req: NewApplicationRequest)                    extends Action[Error Xor JobApplication]
  case class UpdateApplication(email: Email, req: UpdateApplicationRequest) extends Action[Error Xor JobApplication]
  case class RemoveApplication(email: Email)                                extends Action[Boolean]
  case class CheckPIN(email: Email, pin: PIN)                               extends Action[Boolean]

  type Program[A]   = Free[Action, A]
  type ProgramEx[A] = XorT[Program, Error, A]

  private def execute[A](action: Action[A])               = Free.liftF(action)
  private def returns[A](value: A): Program[A]            = Free.pure(value)
  private def fail[A](error: Error): Program[Error Xor A] = returns(Left(error))

  private def executeOrFail[A](action: Action[Error Xor A]): ProgramEx[A] = XorT(execute(action))
  private def executeRight[A](action: Action[A]): ProgramEx[A]            = XorT.right(execute(action))
  private def returnOrFail[A](value: Error Xor A): ProgramEx[A]           = XorT.fromXor(value)
  private def throws[A](error: Error): ProgramEx[A]                       = XorT(fail(error))

  def applyForJob(req: NewApplicationRequest): Program[Error Xor JobApplication] = validate(req) match {
    case Left(error) => fail(error)
    case Right(_)    => execute(SaveApplication(req))
  }

  def updateApplication(email: Email, pin: PIN, req: UpdateApplicationRequest): ProgramEx[JobApplication] =
    for {
      _          <- returnOrFail(validate(req))
      correctPIN <- executeRight(CheckPIN(email, pin))
      result     <- if (correctPIN) executeOrFail(UpdateApplication(email, req)) else throws(IncorrectPIN)
    } yield result

  def deleteApplication(email: Email, pin: PIN): ProgramEx[Boolean] =
    for {
      _          <- returnOrFail(validateEmail(email))
      correctPIN <- executeRight(CheckPIN(email, pin))
      result     <- if (correctPIN) executeRight(RemoveApplication(email)) else throws(IncorrectPIN)
    } yield result

  private def validate(request: UpdateApplicationRequest): RequestError Xor UpdateApplicationRequest =
    for {
      _ <- request.newFullName.traverse(validateFullName)
      _ <- request.newCv.traverse(validateCv)
      _ <- request.newMotivationLetter.traverse(validateLetter)
    } yield request

  private def validate(request: NewApplicationRequest): RequestError Xor NewApplicationRequest =
    for {
      _ <- validateEmail(request.email)
      _ <- validateFullName(request.fullName)
      _ <- validateCv(request.cv)
      _ <- validateLetter(request.motivationLetter)
    } yield request

  private def validateEmail(email: Email) = email match {
    case Email.Valid() => Right(email)
    case _             => Left(EmailFormatError)
  }

  private def validateFullName(fullName: FullName) = if (fullName.value.trim.isEmpty) Left(Empty("fullName")) else Right(fullName)
  private def validateCv(cv: CV)                   = if (cv.value.isEmpty) Left(Empty("cv")) else Right(cv)
  private def validateLetter(letter: Letter)       = if (letter.value.isEmpty) Left(Empty("letter")) else Right(letter)

}
