package com.wanderlust.persistence

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Try
import scala.util.Success
import scala.util.Failure

import com.datastax.driver.core.Row
import com.datastax.driver.core.Session

import com.github.nscala_time.time.Imports.DateTime

import com.wanderlust.model.DataModel.Story
import com.wanderlust.model.DataModel.CqlStrings

// can I use sealed
sealed trait StoryRepository {
  def createStory
  def updateStory
  def deleteStory
  def getStoryById(storyId: Int): Future[Option[Story]]
}

class StoryRepositoryImpl extends StoryRepository {
  self: Connector[Session] =>
    
  implicit val ec: ExecutionContext = ExecutionContext.global
  implicit val session: Session = getSession

  //sections: List[Section], categories: List[Category], accompanyList: Option[List[String]], externalLinks: Option[List[String]])
  def mapRowToStory (row: Row): Option[Story] = {
    val date = row.getDate("date")
    Option(Story(row.getUUID("id"), new DateTime(date.getYear, date.getMonth, date.getDay, 0, 0), row.getString("place"), row.getString("title"), List.empty, List.empty, None, None))
  }
  
  def getStoryById(storyId: Int): Future[Option[Story]] = {

    val getStoryByIdPS = cql"SELECT * FROM story_detail WHERE place = ? AND userId = ? AND id = ?"
    val boundedStatement = getStoryByIdPS.bind("Kolkata", "100", "1")
    val resultFuture = Future(session.execute(boundedStatement))

    resultFuture.map(rs => mapRowToStory(rs.one()))
  }
  def createStory: Unit = {
    ???
  }

  def deleteStory: Unit = {
    ???
  }

  def updateStory: Unit = {
    ???
  }
}
