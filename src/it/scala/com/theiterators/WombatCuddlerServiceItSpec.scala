package com.theiterators

import com.theiterators.wombatcuddler.main.H2Driver.api._
import com.theiterators.wombatcuddler.services.Service

import scala.language.higherKinds

trait WombatCuddlerServiceItSpec extends WombatCuddlerDbItSpec {

  protected def test[DSL[_], R](service: Service[DSL, DBIO])(program: service.Program[R]) = {
    val dbActions = service execute program
    testWithRollback(dbActions)
  }
}
