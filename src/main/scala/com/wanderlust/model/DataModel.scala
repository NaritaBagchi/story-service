package com.wanderlust.model

import spray.json.DefaultJsonProtocol._

import com.github.nscala_time.time.Imports.DateTime
import spray.json.RootJsonFormat
import spray.json.JsObject
import spray.json.JsString
import spray.json.JsNumber
import spray.json.JsValue
import com.datastax.driver.core.SimpleStatement
import com.datastax.driver.core.PreparedStatement
import com.datastax.driver.core.Session
import java.util.UUID

// TODO:
// accompanyList: List[String] to become List[User/Member],
// Place to become GeoLocation
// Categorize the Optional and Mandatory fields

object DataModel {

  object Categories extends Enumeration {
    type Categories = Value
    val Native, Solo, Family, Friends, Bachelor, Bachelorette, Honeymoon, Summers, Trek, Hike, Beach, Hills, Mountains, Shopping, WithPet = Value
  }
  type Category = Categories.Value
  //import Category._
  final case class Section(script: String, date: Option[DateTime], place: Option[String], photos: Option[String])
  final case class Story(id: UUID, date: DateTime, place: String, title: String, sections: List[Section],
                         categories: List[Category], accompanyList: Option[List[String]], externalLinks: Option[List[String]])
  final case class StoryList(items: List[Story])

  implicit class CqlStrings( final val strContext: StringContext) extends AnyVal {
    def cql(args: Any*)(implicit session: Session): PreparedStatement = {
      val statement = new SimpleStatement(strContext.raw(args: _*))
      session.prepare(statement)
    }
  }

  implicit object UUIDFormat extends RootJsonFormat[UUID] {
    def write(uuid: UUID) = JsString(uuid.toString)
    def read(value: JsValue) = {
      value match {
        case JsString(uuid) => UUID.fromString(uuid)
        case _              => throw new Exception("Expected hexadecimal UUID string")
      }
    }
  }

  implicit object JodaTimeJsonFormat extends RootJsonFormat[DateTime] {
    def write(dt: DateTime) = {
      JsObject("day" -> JsNumber(dt.getDayOfMonth), "month" -> JsNumber(dt.getMonthOfYear), "year" -> JsNumber(dt.getYear))
    }

    def read(value: JsValue) = {
      value.asJsObject.getFields("day", "month", "year") match {
        case Seq(JsString(day), JsString(month), JsString(year)) => new DateTime(year.toInt, month.toInt, day.toInt, 0, 0)
        case _ => throw new Exception("Deserialization Exception")
      }
    }
  }
  implicit def enumFormat[T <: Enumeration](implicit enu: T): RootJsonFormat[T#Value] =
    new RootJsonFormat[T#Value] {
      def write(obj: T#Value): JsValue = JsString(obj.toString)
      def read(json: JsValue): T#Value = {
        json match {
          case JsString(txt) => enu.withName(txt)
          case somethingElse => throw new Exception(s"Deserialization Exception - Expected a value from enum $enu instead of $somethingElse")
        }
      }
    }

  implicit val categoryFormat: RootJsonFormat[Category] = enumFormat(Categories)

  implicit val sectionFormat = jsonFormat4(Section)
  implicit val storyFormat = jsonFormat8(Story)
}