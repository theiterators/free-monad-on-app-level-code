package com.theiterators.wombatcuddler.main

import akka.http.scaladsl.server.Route
import com.theiterators.wombatcuddler.resources.CuddlerResource

trait RestInterface extends Resources {
  def routes: Route = applyForJob ~ myApplication
}

trait Resources extends CuddlerResource
