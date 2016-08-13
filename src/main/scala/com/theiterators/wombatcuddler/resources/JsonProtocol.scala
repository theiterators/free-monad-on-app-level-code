package com.theiterators.wombatcuddler.resources

import com.theiterators.wombatcuddler.domain._
import play.api.libs.functional.syntax._
import play.api.libs.json._

trait JsonProtocol {
  implicit val errorWrites = new Writes[Error] {
    override def writes(o: Error): JsValue = Json.obj("error" -> o.errorCode)
  }

  implicit val emailFormat = new Format[Email] {
    override def writes(o: Email) = JsString(o.value)
    override def reads(json: JsValue) = json match {
      case JsString(value) => JsSuccess(Email(value))
      case _               => JsError()
    }
  }
  implicit val cvFormat = new Format[CV] {
    override def writes(o: CV) = JsString(o.value)
    override def reads(json: JsValue) = json match {
      case JsString(value) => JsSuccess(CV(value))
      case _               => JsError()
    }
  }
  implicit val fullNameFormat = new Format[FullName] {
    override def writes(o: FullName) = JsString(o.value)
    override def reads(json: JsValue) = json match {
      case JsString(value) => JsSuccess(FullName(value))
      case _               => JsError()
    }
  }
  implicit val letterFormat = new Format[Letter] {
    override def writes(o: Letter) = JsString(o.value)
    override def reads(json: JsValue) = json match {
      case JsString(value) => JsSuccess(Letter(value))
      case _               => JsError()
    }
  }

  implicit val newApplicationRequestRead: Reads[NewApplicationRequest] = (
      (__ \ "email").read[Email] and (__ \ "fullName").read[FullName] and (__ \ "cv").read[CV] and (__ \ "letter").read[Letter]
  )(NewApplicationRequest.apply _)
  implicit val updateApplicationRequestReads: Reads[UpdateApplicationRequest] = (
      (__ \ "fullName").readNullable[FullName] and (__ \ "cv").readNullable[CV] and (__ \ "letter").readNullable[Letter]
  )(UpdateApplicationRequest.apply _)
}
