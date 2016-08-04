package com.theiterators.wombatcuddler.domain

case class NewApplicationRequest(email: Email, fullName: FullName, cv: CV, motivationLetter: Letter)
case class UpdateApplicationRequest(newFullName: Option[FullName] = None,
                                    newCv: Option[CV] = None,
                                    newMotivationLetter: Option[Letter] = None)
