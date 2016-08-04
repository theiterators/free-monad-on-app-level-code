package com.theiterators.wombatcuddler.main

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, Materializer}

import scala.concurrent.ExecutionContext

object Main extends Setup with Server with RestInterface {
  override implicit val system: ActorSystem                = ActorSystem("wombat-cuddler")
  override implicit val executionContext: ExecutionContext = system.dispatcher
  override implicit val materializer: Materializer         = ActorMaterializer()

  def main(args: Array[String]): Unit = {
    Http()
      .bindAndHandle(handler = routes, interface = httpServerConfig.hostname, port = httpServerConfig.port)
      .map { binding =>
        logger.info(s"HTTP server started at ${binding.localAddress}")
      }
      .recover { case ex => logger.error(ex, "Could not start HTTP server") }
  }
}
