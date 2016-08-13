package com.theiterators.wombatcuddler.repository

import java.sql.Timestamp
import java.time.LocalDateTime

import com.theiterators.wombatcuddler.domain._
import com.theiterators.wombatcuddler.main.H2Driver.api._

object TypeMappers {
  implicit val emailMapping: BaseColumnType[Email]       = MappedColumnType.base(_.value, Email.apply)
  implicit val cvMapping: BaseColumnType[CV]             = MappedColumnType.base(_.value, CV.apply)
  implicit val fullnameMapping: BaseColumnType[FullName] = MappedColumnType.base(_.value, FullName.apply)
  implicit val letterMapping: BaseColumnType[Letter]     = MappedColumnType.base(_.value, Letter.apply)
  implicit val pinMapping: BaseColumnType[PIN]           = MappedColumnType.base(_.value, PIN.apply)
  implicit val localDateTimeMapping: BaseColumnType[LocalDateTime] =
    MappedColumnType.base(Timestamp.valueOf, (ts: Timestamp) => ts.toLocalDateTime)
}
