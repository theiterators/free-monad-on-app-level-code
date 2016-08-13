package com.theiterators.wombatcuddler.repository

import java.time.LocalDateTime

import com.theiterators.wombatcuddler.domain._
import com.theiterators.wombatcuddler.main.H2Driver.api._
import com.theiterators.wombatcuddler.repository.TypeMappers._
import slick.lifted.ProvenShape

class JobApplications(tag: Tag) extends Table[JobApplicationRow](tag, Some("PUBLIC"), "JOB_APPLICATIONS") {
  def id: Rep[JobApplicationId]             = column[JobApplicationId]("ID", O.PrimaryKey, O.AutoInc)
  def email: Rep[Email]                     = column[Email]("EMAIL")
  def fullName: Rep[FullName]               = column[FullName]("FULL_NAME")
  def cv: Rep[CV]                           = column[CV]("CV", O.SqlType("CLOB"))
  def motivationLetter: Rep[Letter]         = column[Letter]("MOTIVATION_LETTER", O.SqlType("CLOB"))
  def pin: Rep[PIN]                         = column[PIN]("PIN")
  def createdAt: Rep[LocalDateTime]         = column[LocalDateTime]("CREATED_AT")
  def updatedAt: Rep[Option[LocalDateTime]] = column[Option[LocalDateTime]]("UPDATED_AT")

  override def * : ProvenShape[JobApplicationRow] =
    (id.?, email, pin, fullName, cv, motivationLetter, createdAt, updatedAt) <> (JobApplicationRow.tupled, JobApplicationRow.unapply)
}
