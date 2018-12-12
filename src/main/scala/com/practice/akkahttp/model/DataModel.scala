package com.practice.akkahttp.model

import spray.json.DefaultJsonProtocol._

import com.github.nscala_time.time.Imports.DateTime
import spray.json.RootJsonFormat
import spray.json.JsObject
import spray.json.JsString
import spray.json.JsNumber
import spray.json.JsValue

// TODO: 
// accompanyList: List[String] to become List[User/Member], 
// Place to become GeoLocation
// Categorize the Optional and Mandatory fields

object DataModel {

  final case class Section(date: DateTime, place: String, photos: String, script: String)
  final case class Story(id: Int, date: DateTime, place: String, title: String, accompanyList: List[String],
                         categories: String, externalLinks: List[String], sections: List[Section])
  final case class StoryList(items: List[Story])

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
  implicit val sectionFormat = jsonFormat4(Section)
  implicit val storyFormat = jsonFormat8(Story)
}