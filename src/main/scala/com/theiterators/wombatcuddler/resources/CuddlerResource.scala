package com.theiterators.wombatcuddler.resources

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{Directives, Route}
import com.theiterators.wombatcuddler.actions.Cuddler
import com.theiterators.wombatcuddler.domain._
import com.theiterators.wombatcuddler.services.Service
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport

import scala.concurrent.{ExecutionContext, Future}

trait CuddlerResource extends Directives with JsonProtocol with PlayJsonSupport {
  implicit def executionContext: ExecutionContext

  def cuddlerService: Service[Cuddler.Action, Future]

  val applyForJob: Route = (pathPrefix("cuddlers") & pathEndOrSingleSlash & post & entity(as[NewApplicationRequest])) { req =>
    onSuccess(cuddlerService.run(Cuddler.applyForJob(req))) {
      case Right(_)             => complete(NoContent)
      case Left(DuplicateEmail) => complete(Conflict)
      case Left(error)          => complete(BadRequest -> error)
    }
  }

  val myApplication: Route = (pathPrefix("my-application") & pathEndOrSingleSlash & parameters(('email.as[String], 'pin.as[String]))) {
    (emailStr, pinStr) =>
      val email = Email(emailStr)
      val pin   = PIN(pinStr)

      (put & entity(as[UpdateApplicationRequest])) { req =>
        onSuccess(cuddlerService.run(Cuddler.updateApplication(email, pin, req).value)) {
          case Right(_)            => complete(NoContent)
          case Left(IncorrectPIN)  => complete(Forbidden)
          case Left(EmailNotFound) => complete(NotFound)
          case Left(error)         => complete(BadRequest -> error)
        }
      } ~ delete {
        onSuccess(cuddlerService.run(Cuddler.deleteApplication(email, pin).value)) {
          case Right(true)                        => complete(NoContent)
          case Right(false) | Left(EmailNotFound) => complete(NotFound)
          case Left(IncorrectPIN)                 => complete(Forbidden)
          case Left(error)                        => complete(BadRequest -> error)
        }
      }
  }

}
