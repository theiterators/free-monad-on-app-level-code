package com.theiterators.wombatcuddler.main

import slick.driver.H2Driver.api._

trait Setup {
  lazy val db = Database.forURL("jdbc:h2:mem:wombatcuddlers;DB_CLOSE_DELAY=-1")
}
