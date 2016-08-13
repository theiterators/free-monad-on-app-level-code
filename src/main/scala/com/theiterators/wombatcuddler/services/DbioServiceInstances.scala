package com.theiterators.wombatcuddler.services

import cats.Monad
import com.theiterators.wombatcuddler.main.H2Driver.api._
import com.theiterators.wombatcuddler.utils.DbioMonad

import scala.concurrent.{ExecutionContext, Future}
import scala.language.{higherKinds, implicitConversions}

trait DbioServiceInstances extends DbioMonad {
  def db: Database
  implicit def executionContext: ExecutionContext

  implicit def toFuture[DSL[_]](dbioService: Service[DSL, DBIO]): Service[DSL, Future] = new Service[DSL, Future] {
    override def execute[A](program: Program[A])(implicit M: Monad[Future]): Future[A] = db.run(dbioService.execute(program))
  }
}
