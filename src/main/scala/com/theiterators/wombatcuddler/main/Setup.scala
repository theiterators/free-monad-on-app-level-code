package com.theiterators.wombatcuddler.main

import com.theiterators.wombatcuddler.actions.Cuddler
import com.theiterators.wombatcuddler.main.H2Driver.api._
import com.theiterators.wombatcuddler.repository.JobApplicationRepository
import com.theiterators.wombatcuddler.services._

import scala.concurrent.{ExecutionContext, Future}

trait Setup extends DbioServiceInstances {
  implicit def executionContext: ExecutionContext
  lazy val db = Database.forDriver(new org.h2.Driver, "jdbc:h2:./wombatcuddlers")

  lazy val cuddlerService: Service[Cuddler.Action, Future] = CuddlerService(new JobApplicationRepository)
}
