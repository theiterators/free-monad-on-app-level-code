package com.theiterators.wombatcuddler

import cats.data.Xor.{Left, Right}
import cats.{Id, ~>}
import com.theiterators.wombatcuddler.actions.Cuddler
import com.theiterators.wombatcuddler.actions.Cuddler._
import com.theiterators.wombatcuddler.domain._
import org.scalatest.{FunSuite, Matchers}

class CuddlerSpec extends FunSuite with Matchers {

  case object FakeJobApplication extends JobApplication {
    override def email: Email             = Email("joe.roberts@example.com")
    override def fullName: FullName       = FullName("Joe Roberts")
    override def cv: CV                   = CV("My name is Joe Roberts I work for the State")
    override def motivationLetter: Letter = Letter("I dreamt all my life about cuddling a wombat")
    override def pin: PIN                 = PIN("1000")
  }
  import Cuddler._

  val testInterpreter = new (Action ~> Id) {
    override def apply[A](fa: Action[A]): A = fa match {
      case SaveApplication(_)                                  => Right(FakeJobApplication)
      case UpdateApplication(_, _)                             => Right(FakeJobApplication)
      case CheckPIN(Email("joe.roberts@example.com"), pin)     => pin == PIN("1000")
      case CheckPIN(_, _)                                      => false
      case RemoveApplication(Email("joe.roberts@example.com")) => true
      case RemoveApplication(_)                                => false
    }
  }

  val newApplicationRequest = NewApplicationRequest(Email("joe.roberts@example.com"),
                                                    FullName("Joe Roberts"),
                                                    CV("My name is Joe Roberts I work for the State"),
                                                    Letter("I dreamt all my life about cuddling a wombat"))

  test("Trying to apply for wombat cuddler and giving invalid e-mail is a no-go") {
    applyForJob(newApplicationRequest.copy(email = Email("joe.roberts2example.com"))) foldMap testInterpreter shouldBe Left(
        EmailFormatError)
  }

  test("Trying to apply for wombat cuddler and giving empty name is a no-go") {
    applyForJob(newApplicationRequest.copy(fullName = FullName(""))) foldMap testInterpreter shouldBe Left(Empty("fullName"))
  }

  test("Trying to apply for wombat cuddler and giving empty CV is a no-go") {
    applyForJob(newApplicationRequest.copy(cv = CV(""))) foldMap testInterpreter shouldBe Left(Empty("cv"))
  }

  test("Trying to apply for wombat cuddler and giving empty motivation letter CV is a no-go") {
    applyForJob(newApplicationRequest.copy(motivationLetter = Letter(""))) foldMap testInterpreter shouldBe Left(Empty("letter"))
  }

  test("otherwise application can be processed") {
    applyForJob(newApplicationRequest) foldMap testInterpreter shouldBe Right(FakeJobApplication)
  }

  test("Trying to update job application with wrong data should fail") {
    updateApplication(Email("joe.roberts@example.com"), pin = PIN("1000"), UpdateApplicationRequest(newCv = Some(CV("")))).value foldMap testInterpreter shouldBe Left(
        Empty("cv"))
  }

  test("Trying to update job application giving wrong PIN should fail") {
    updateApplication(Email("joe.roberts@example.com"), pin = PIN("1111"), UpdateApplicationRequest()).value foldMap testInterpreter shouldBe Left(
        IncorrectPIN)
  }

  test("Trying to update job application giving email that was not registered should fail") {
    updateApplication(Email("john.doe@example.com"), pin = PIN("1000"), UpdateApplicationRequest()).value foldMap testInterpreter shouldBe Left(
        IncorrectPIN)
  }

  test("otherwise it should succeed") {
    updateApplication(Email("joe.roberts@example.com"), pin = PIN("1000"), UpdateApplicationRequest()).value foldMap testInterpreter shouldBe Right(
        FakeJobApplication)
  }

  test("Trying to remove job application giving wrong PIN should fail") {
    deleteApplication(Email("joe.roberts@example.com"), pin = PIN("0000")).value foldMap testInterpreter shouldBe Left(IncorrectPIN)
  }

  test("Trying to remove job application giving email that was not registered should fail") {
    deleteApplication(Email("joe@example.com"), pin = PIN("1000")).value foldMap testInterpreter shouldBe Left(IncorrectPIN)
  }

  test("otherwise it should remove an application") {
    deleteApplication(Email("joe.roberts@example.com"), pin = PIN("1000")).value foldMap testInterpreter shouldBe Right(true)
  }

}
