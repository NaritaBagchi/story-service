package com.wanderlust.server

import scala.io.StdIn
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

object ServerLauncher {
  def main(args: Array[String]) {

    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    //========
    val sections = List(Section("enjoyed to vaca", Some(new DateTime(2017, 12, 4, 0, 0)), Some("ccu"), Some("photo1")))
    val stories: List[Story] = List(Story(UUID.randomUUID(), new DateTime(2017, 12, 4, 0, 0), "ccu", "home title", sections, List.empty, None, None))
    def fetchStory(itemId: Long): Future[Option[Story]] = Future {
      stories.find(o => o.id == itemId)
    }

    //=========
    val route =
      pathPrefix("stories" / IntNumber) { userId =>
        pathEnd {
          get {
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
          }
        } ~
          path(IntNumber) { storyId =>
            pathEnd {
              get {
                val storyFut: Future[Option[Story]] = fetchStory(storyId)
                onSuccess(storyFut) {
                  case Some(item) => complete(item)
                  case None       => complete(StatusCodes.NotFound)
                }
              }
            }
          }
      }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8081)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
