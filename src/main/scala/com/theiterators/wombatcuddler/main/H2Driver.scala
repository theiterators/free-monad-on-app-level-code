package com.theiterators.wombatcuddler.main

import java.sql.SQLException

import org.h2.jdbc.JdbcSQLException

object H2Driver extends slick.driver.H2Driver {
  object H2API extends API {
    final class DuplicateKey(reason: String, cause: Throwable) extends SQLException(reason, DuplicateKey.DUPLICATE_KEY, cause)

    object DuplicateKey {
      val DUPLICATE_KEY = "23505"

      def unapply(ex: Throwable): Option[DuplicateKey] = ex match {
        case (jdbcException: JdbcSQLException) =>
          jdbcException.getSQLState match {
            case DUPLICATE_KEY => Some(new DuplicateKey(jdbcException.getMessage, jdbcException))
            case _             => None
          }
        case _ => None
      }
    }
  }

  override val api = H2API
}
