package com.theiterators

import com.theiterators.wombatcuddler.main.H2Driver.api._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object WombatCuddlerDbItSpec {
  private case class IntentionalRollbackException[R](result: R) extends Exception("Rolling back transaction after test")

  implicit class RunWithRollback(val db: Database) extends AnyVal {
    def runWithRollback[R](a: DBIOAction[R, NoStream, Nothing])(implicit ec: ExecutionContext): Future[R] = {
      val actionWithRollback = a flatMap (r => DBIO.failed(IntentionalRollbackException(r)))
      val testResult         = db run actionWithRollback.transactionally.asTry
      testResult map {
        case Failure(IntentionalRollbackException(success)) => success.asInstanceOf[R]
        case Failure(t)                                     => throw t
        case Success(r)                                     => r
      }
    }
  }
}

trait WombatCuddlerDbItSpec extends WombatCuddlerItSpec {
  import WombatCuddlerDbItSpec._

  override lazy val db = Database.forDriver(new org.h2.Driver, "jdbc:h2:./wombatcuddlers-it")
  protected def testWithRollback[A](action: DBIO[A]) = await(db runWithRollback action)
}
