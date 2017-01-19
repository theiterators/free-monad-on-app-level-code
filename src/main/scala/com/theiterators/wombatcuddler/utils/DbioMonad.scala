package com.theiterators.wombatcuddler.utils

import cats.Monad
import com.theiterators.wombatcuddler.main.H2Driver.api._

import scala.concurrent.ExecutionContext

trait DbioMonad {
  implicit def DBIOMonad(implicit executionContext: ExecutionContext): Monad[DBIO] = new Monad[DBIO] {
    override def pure[A](x: A): DBIO[A]                                 = DBIO.successful(x)
    override def flatMap[A, B](fa: DBIO[A])(f: (A) => DBIO[B]): DBIO[B] = fa flatMap f
    override def tailRecM[A, B](a: A)(f: (A) => DBIO[Either[A, B]]): DBIO[B] = f(a) flatMap {
      case Left(a1) => tailRecM(a1)(f)
      case Right(b) => DBIO.successful(b)
    }
  }
}

object DbioMonad extends DbioMonad
