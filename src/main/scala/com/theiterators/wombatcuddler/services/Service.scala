package com.theiterators.wombatcuddler.services

import cats.free.Free
import cats.{Monad, ~>}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

trait Service[DSL[_], M[_]] { self =>
  final type Program[Result] = Free[DSL, Result]

  def execute[A](program: Program[A])(implicit M: Monad[M]): M[A]
}

object Service {
  def apply[DSL[_], M[_]](f: DSL ~> M): FreeService[DSL, M] = new FreeService[DSL, M] {
    override def apply[A](fa: DSL[A]): M[A] = f(fa)
  }

  implicit class FutureServiceOps[DSL[_]](val self: Service[DSL, Future]) extends AnyVal {
    import cats.std.future._
    def run[A](action: self.Program[A])(implicit ec: ExecutionContext): Future[A] = self.execute(action)
    def runWithResultHandler[A, U](action: self.Program[A])(handler: PartialFunction[A, U])(implicit ec: ExecutionContext): Future[A] = {
      val fut = run(action)
      fut.onSuccess(handler)
      fut
    }
  }
}

abstract class FreeService[DSL[_], M[_]] extends (DSL ~> M) with Service[DSL, M] { self =>
  final val nat: DSL ~> M = this
  override final def execute[A](program: Program[A])(implicit M: Monad[M]): M[A] = program foldMap this
}
