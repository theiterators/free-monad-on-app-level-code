package com.theiterators.wombatcuddler.main

import akka.http.scaladsl.server.Route

trait RestInterface extends Resources {
  def routes: Route = ???
}

trait Resources
