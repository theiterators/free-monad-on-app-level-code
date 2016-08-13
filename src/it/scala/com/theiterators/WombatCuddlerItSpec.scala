package com.theiterators

import java.util.concurrent.Executors

import com.theiterators.wombatcuddler.main.Setup
import com.theiterators.wombatcuddler.utils.DbioMonad

import scala.concurrent._
import scala.concurrent.duration._

trait WombatCuddlerItSpec extends Setup with DbioMonad {
  override implicit val executionContext: ExecutionContext = ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor())

  protected final def await[T](f: Future[T]): T = Await.result(f, 1.minute)
}
