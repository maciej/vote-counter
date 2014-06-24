package com.softwaremill.votecounter.web

import spray.routing.{HttpService, SimpleRoutingApp}
import akka.actor.{Actor, ActorSystem}
import akka.io.IO
import spray.can.Http
import com.softwaremill.votecounter.infrastructure.Beans
import com.softwaremill.votecounter.db.VotesDao
import spray.httpx.Json4sJacksonSupport
import org.json4s.DefaultFormats
import java.text.SimpleDateFormat
import java.util.Locale
import org.json4s.ext.JodaTimeSerializers

class VoteCounterWebService(beans: Beans) extends Actor with VoteService {
  implicit def actorRefFactory = context

  override protected val voteDao: VotesDao = beans.voteDao

  def receive = runRoute(voteRoute)

}

case class IntHolder(number: Int)

trait VoteService extends HttpService with Json4sJacksonSupport {

  implicit def json4sJacksonFormats = new DefaultFormats {
    override protected def dateFormatter =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
  } ++ JodaTimeSerializers.all

  protected val voteDao: VotesDao

  def voteRoute = pathPrefix("votes") {
    path("") {
      get {
        complete {
          voteDao.findAll()
        }
      }
    }
  }

}

object VoteCounterWeb extends App with SimpleRoutingApp {
  implicit val system = ActorSystem("vc-main")

  val beans = Beans
  val dbInitializer = beans.dbInitializer
  val testDataPopulator = beans.testDataPopulator
  val conferenceDataInitializer = beans.conferenceDataInitializer

  dbInitializer.initializeAndBlock()
  conferenceDataInitializer.initializeAndBlock()
  testDataPopulator.populateWithTestData()
  

  IO(Http) ! Http.Bind(beans.webHandler, interface = "localhost", port = 8080)
}
