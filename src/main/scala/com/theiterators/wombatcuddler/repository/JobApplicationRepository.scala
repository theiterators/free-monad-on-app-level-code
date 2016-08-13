package com.theiterators.wombatcuddler.repository

import com.theiterators.wombatcuddler.domain.{Email, PIN}
import com.theiterators.wombatcuddler.main.H2Driver.api._

import scala.concurrent.ExecutionContext

final class JobApplicationRepository {
  import TypeMappers.{emailMapping, pinMapping}
  val jobApplications                 = TableQuery[JobApplications]
  private val applicationsReturningId = jobApplications returning (jobApplications map (_.id))

  private val findQuery        = Compiled((id: Rep[JobApplicationId]) => jobApplications filter (_.id === id))
  private val findByEmailQuery = Compiled((email: Rep[Email]) => jobApplications filter (_.email === email))
  private val existsByEmailAndPINQuery = Compiled(
      (email: Rep[Email], pin: Rep[PIN]) => jobApplications.filter(row => row.email === email && row.pin === pin).exists)

  def delete(email: Email): DBIO[Int]                             = findByEmailQuery(email).delete
  def exists(email: Email, pin: PIN): DBIO[Boolean]               = existsByEmailAndPINQuery((email, pin)).result
  def find(id: JobApplicationId): DBIO[Option[JobApplicationRow]] = findQuery(id).result.headOption
  def findByEmail(email: Email): DBIO[Option[JobApplicationRow]]  = findByEmailQuery(email).result.headOption
  def findExisting(id: JobApplicationId): DBIO[JobApplicationRow] = findQuery(id).result.head
  def save(row: JobApplicationRow)(implicit ec: ExecutionContext): DBIO[JobApplicationId] =
    applicationsReturningId.insertOrUpdate(row).map {
      case Some(newId) => newId
      case None        => row.id.getOrElse(sys.error(s"ReturningInsertActionComposer updated entity ($row) with no id"))
    }
}
