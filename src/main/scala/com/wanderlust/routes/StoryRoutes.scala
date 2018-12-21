package com.wanderlust.routes

import scala.concurrent.Future
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import com.github.nscala_time.time.Imports.DateTime
import java.util.UUID

import com.wanderlust.model.DataModel._
import com.wanderlust.persistence.CassandraConnector
import com.wanderlust.persistence.StoryRepositoryImpl

object StoryRoutes {
  val storyRepo = new StoryRepositoryImpl with CassandraConnector
  // lazy val
   val storyRoute =
      pathPrefix("stories" / IntNumber) { userId =>
        pathEnd {
          get {
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
          }
        } ~
          path(IntNumber) { storyId =>
            pathEnd {
              get {
                val storyFut: Future[Option[Story]] = storyRepo.getStoryById(storyId)
                // check akka directive onComplete
                onSuccess(storyFut) {
                  case Some(item) => complete(item)
                  case None       => complete(StatusCodes.NotFound)
                }
              }
            }
          }
      }
}