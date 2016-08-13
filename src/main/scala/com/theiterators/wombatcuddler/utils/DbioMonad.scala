package com.theiterators.wombatcuddler.utils

import cats.Monad
import com.theiterators.wombatcuddler.main.H2Driver.api._

import scala.concurrent.ExecutionContext

trait DbioMonad {
  implicit def DBIOMonad(implicit executionContext: ExecutionContext): Monad[DBIO] = new Monad[DBIO] {
    override def pure[A](x: A): DBIO[A]                                 = DBIO.successful(x)
    override def flatMap[A, B](fa: DBIO[A])(f: (A) => DBIO[B]): DBIO[B] = fa flatMap f
  }
}

object DbioMonad extends DbioMonad
