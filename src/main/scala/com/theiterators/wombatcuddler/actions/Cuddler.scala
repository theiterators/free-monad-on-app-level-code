package com.theiterators.wombatcuddler.actions

import cats.data.EitherT
import cats.free.Free
import cats.instances.either._
import cats.instances.option._
import cats.syntax.either._
import cats.syntax.traverse._
import com.theiterators.wombatcuddler.domain._

object Cuddler {
  sealed trait Action[R]
  case class SaveApplication(req: NewApplicationRequest)                    extends Action[Either[Error, JobApplication]]
  case class UpdateApplication(email: Email, req: UpdateApplicationRequest) extends Action[Either[Error, JobApplication]]
  case class RemoveApplication(email: Email)                                extends Action[Boolean]
  case class CheckPIN(email: Email, pin: PIN)                               extends Action[Boolean]

  type Program[A]   = Free[Action, A]
  type ProgramEx[A] = EitherT[Program, Error, A]

  private def execute[A](action: Action[A])                    = Free.liftF(action)
  private def returns[A](value: A): Program[A]                 = Free.pure(value)
  private def fail[A](error: Error): Program[Either[Error, A]] = returns(Left(error))

  private def executeOrFail[A](action: Action[Either[Error, A]]): ProgramEx[A] = EitherT(execute(action))
  private def executeRight[A](action: Action[A]): ProgramEx[A]                 = EitherT.right(execute(action))
  private def returnOrFail[A](value: Either[Error, A]): ProgramEx[A]           = EitherT.fromEither(value)
  private def throws[A](error: Error): ProgramEx[A]                            = EitherT(fail(error))

  def applyForJob(req: NewApplicationRequest): Program[Either[Error, JobApplication]] = validate(req) match {
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

  private def validate(request: UpdateApplicationRequest): Either[RequestError, UpdateApplicationRequest] =
    for {
      _ <- request.newFullName.traverse(validateFullName)
      _ <- request.newCv.traverse(validateCv)
      _ <- request.newMotivationLetter.traverse(validateLetter)
    } yield request

  private def validate(request: NewApplicationRequest): Either[RequestError, NewApplicationRequest] =
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
