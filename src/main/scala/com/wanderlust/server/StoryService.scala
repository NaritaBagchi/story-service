package com.wanderlust.server

import scala.concurrent.Future
import com.wanderlust.persistence.StoryRepository

sealed trait StoryService {
  def createStory
  def updateStory
  def deleteStory
  def getStory
}

class StoryServiceImpl extends StoryService {
  self: StoryRepository => 

  def getStory = {
    getStoryById
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