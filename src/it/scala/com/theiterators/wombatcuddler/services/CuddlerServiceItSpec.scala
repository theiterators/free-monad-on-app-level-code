package com.theiterators.wombatcuddler.services

import cats.data.Xor.{Left, Right}
import com.theiterators.WombatCuddlerServiceItSpec
import com.theiterators.wombatcuddler.actions.Cuddler._
import com.theiterators.wombatcuddler.domain._
import com.theiterators.wombatcuddler.repository.{JobApplicationRepository, JobApplicationRow}
import org.scalatest._

class CuddlerServiceItSpec extends FunSpec with Matchers with Inside with WombatCuddlerServiceItSpec {
  val newApplicationRequest = NewApplicationRequest(Email("joe.roberts@example.com"),
                                                    FullName("Joe Roberts"),
                                                    CV("My name is Joe Roberts I work for the State"),
                                                    Letter("I dreamt all my life about cuddling a wombat"))

  describe("CuddlerService") {
    val cuddlerService = CuddlerService(new JobApplicationRepository)
    //happy path
    it("can save job application request") {
      test(cuddlerService)(applyForJob(newApplicationRequest)) should matchPattern {
        case Right(
            JobApplicationRow(_,
                              Email("joe.roberts@example.com"),
                              _,
                              FullName("Joe Roberts"),
                              CV("My name is Joe Roberts I work for the State"),
                              Letter("I dreamt all my life about cuddling a wombat"),
                              _,
                              None)) =>
      }
    }

    it("can update job application request") {
      test(cuddlerService) {
        for {
          myApplication <- applyForJob(newApplicationRequest)
          pin = myApplication.map(_.pin).getOrElse(PIN("invalid PIN"))
          result <- updateApplication(Email("joe.roberts@example.com"),
                                      pin,
                                      UpdateApplicationRequest(newFullName = Some(FullName("Joe R. Roberts")))).value
        } yield result
      } should matchPattern {
        case Right(
            JobApplicationRow(_,
                              Email("joe.roberts@example.com"),
                              _,
                              FullName("Joe R. Roberts"),
                              CV("My name is Joe Roberts I work for the State"),
                              Letter("I dreamt all my life about cuddling a wombat"),
                              _,
                              Some(_))) =>
      }
    }

    it("can remove job application request") {
      test(cuddlerService) {
        for {
          myApplication <- applyForJob(newApplicationRequest)
          pin = myApplication.map(_.pin).getOrElse(PIN("invalid PIN"))
          _          <- deleteApplication(Email("joe.roberts@example.com"), pin).value
          triedAgain <- applyForJob(newApplicationRequest)
        } yield triedAgain
      } should matchPattern { case Right(_) => }
    }

    // unhappy path
    it("will not add two job applications with the same email") {
      test(cuddlerService) {
        for {
          _      <- applyForJob(newApplicationRequest)
          result <- applyForJob(newApplicationRequest)
        } yield result
      } should matchPattern {
        case Left(DuplicateEmail) =>
      }
    }
  }
}
