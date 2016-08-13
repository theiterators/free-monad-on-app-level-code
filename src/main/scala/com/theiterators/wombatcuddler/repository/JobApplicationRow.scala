package com.theiterators.wombatcuddler.repository

import java.time.LocalDateTime

import com.theiterators.wombatcuddler.domain._
import com.theiterators.wombatcuddler.main.H2Driver.api._

case class JobApplicationId(value: Int) extends AnyVal
object JobApplicationId {
  implicit val mapping: BaseColumnType[JobApplicationId] = MappedColumnType.base(_.value, JobApplicationId.apply)
}

case class JobApplicationRow(id: Option[JobApplicationId] = None,
                             email: Email,
                             pin: PIN,
                             fullName: FullName,
                             cv: CV,
                             motivationLetter: Letter,
                             createdAt: LocalDateTime,
                             updatedAt: Option[LocalDateTime] = None)
    extends JobApplication {
  def update(updateApplicationRequest: UpdateApplicationRequest): JobApplicationRow = this.copy(
      fullName = updateApplicationRequest.newFullName.getOrElse(fullName),
      cv = updateApplicationRequest.newCv.getOrElse(cv),
      motivationLetter = updateApplicationRequest.newMotivationLetter.getOrElse(motivationLetter),
      updatedAt = Some(LocalDateTime.now())
  )
}

object JobApplicationRow {
  def fromNewApplicationRequest(newApplicationRequest: NewApplicationRequest, pin: PIN): JobApplicationRow = JobApplicationRow(
      email = newApplicationRequest.email,
      pin = pin,
      fullName = newApplicationRequest.fullName,
      cv = newApplicationRequest.cv,
      motivationLetter = newApplicationRequest.motivationLetter,
      createdAt = LocalDateTime.now()
  )

  val tupled = (apply _).tupled
}
