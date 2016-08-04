package com.theiterators.wombatcuddler.main

import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.Materializer

import scala.concurrent.ExecutionContext

case class HttpServerConfig(hostname: String, port: Int)

trait Server { self: Setup =>

  implicit def executionContext: ExecutionContext
  implicit def system: ActorSystem
  implicit def materializer: Materializer

  lazy val logger = Logging(system, getClass)

  val httpServerConfig = HttpServerConfig(hostname = "localhost", port = 5000)

}
