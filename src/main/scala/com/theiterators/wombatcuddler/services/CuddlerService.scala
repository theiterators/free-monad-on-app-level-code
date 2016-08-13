package com.theiterators.wombatcuddler.services

import cats.data.Xor
import com.theiterators.wombatcuddler.actions.Cuddler
import com.theiterators.wombatcuddler.actions.Cuddler._
import com.theiterators.wombatcuddler.domain._
import com.theiterators.wombatcuddler.main.H2Driver.api._
import com.theiterators.wombatcuddler.repository._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

abstract class CuddlerService(jobApplicationRepository: JobApplicationRepository)(implicit executionContext: ExecutionContext)
    extends FreeService[Cuddler.Action, DBIO] {
  def saveApplication(newApplicationRequest: NewApplicationRequest): DBIO[Error Xor JobApplication] = {
    val pin            = PIN.generate
    val applicationRow = JobApplicationRow.fromNewApplicationRequest(newApplicationRequest, pin)

    val insertAction = for {
      id          <- jobApplicationRepository.save(applicationRow)
      application <- jobApplicationRepository.findExisting(id)
    } yield application

    insertAction.asTry.flatMap {
      case Success(right)           => DBIO.successful(Xor.Right(right))
      case Failure(DuplicateKey(_)) => DBIO.successful(Xor.Left(DuplicateEmail))
      case Failure(other)           => DBIO.failed(other)
    }
  }

  def checkPIN(email: Email, pin: PIN): DBIO[Boolean] = jobApplicationRepository.exists(email, pin)

  def removeApplication(email: Email): DBIO[Boolean] = jobApplicationRepository.delete(email).map(_ == 1)

  def updateApplication(email: Email, updateApplicationRequest: UpdateApplicationRequest): DBIO[Error Xor JobApplication] =
    jobApplicationRepository
      .findByEmail(email)
      .flatMap {
        case None => DBIO.successful(Xor.Left(EmailNotFound))
        case Some(row) =>
          val updatedRow = row.update(updateApplicationRequest)
          jobApplicationRepository.save(updatedRow).map(_ => Xor.Right(updatedRow))
      }
      .transactionally
}

object CuddlerService {
  def apply(jobApplicationRepository: JobApplicationRepository)(implicit executionContext: ExecutionContext): CuddlerService =
    new CuddlerService(jobApplicationRepository) {
      override def apply[A](fa: Action[A]): DBIO[A] = fa match {
        case SaveApplication(newApplicationRequest)             => saveApplication(newApplicationRequest)
        case CheckPIN(email, pin)                               => checkPIN(email, pin)
        case RemoveApplication(email)                           => removeApplication(email)
        case UpdateApplication(email, updateApplicationRequest) => updateApplication(email, updateApplicationRequest)
      }
    }
}
