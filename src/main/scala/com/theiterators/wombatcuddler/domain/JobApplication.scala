package com.theiterators.wombatcuddler.domain

trait JobApplication {
  def email: Email
  def pin: PIN
  def fullName: FullName
  def cv: CV
  def motivationLetter: Letter
}
