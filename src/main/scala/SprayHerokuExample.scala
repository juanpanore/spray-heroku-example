package com.qmee.sprayherokuexample
import akka.actor.{ActorSystem,Props,Actor}
import akka.io.IO
import akka.pattern.ask
import akka.pattern.pipe
import akka.routing.SmallestMailboxRouter
import akka.util.Timeout
import spray.can.Http
import spray.routing._
import spray.routing.authentication._
import spray.json._
import spray.httpx.SprayJsonSupport._
import spray.util.LoggingContext
import DefaultJsonProtocol._
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class Query(q:String)

object Example extends App { 
  val host = "0.0.0.0"
  val port = Option(System.getenv("PORT")).getOrElse("9000").toInt
  val auth = Option(System.getenv("AUTH")).map(x => UserPass(x,x))
  implicit val system = ActorSystem("example-system")
  val log = LoggingContext.fromActorSystem(system)
  val service = system.actorOf(Props[RoutingActor], "routing-service")
  val reply = system.actorOf(Props[ReplyActor].withRouter(SmallestMailboxRouter(nrOfInstances = 20)))
  implicit val timeout = Timeout(5.seconds)
  log.warning(s"Started Example on port ${port}")
  IO(Http) ? Http.Bind(service, interface = host, port = port)
}

class ReplyActor extends Actor {
  import context.dispatcher
  def receive = {
    case Query(q) => {
      Future(JsObject("query" -> JsString(q),"answer" -> JsString(s"${q} back !"))) pipeTo sender
    }
    case _  => Future(JsObject()) pipeTo sender
  }
}

class RoutingActor extends Actor with ResultService {
  import context.dispatcher
  def actorRefFactory = context
  def receive = runRoute(resultRoute)
  override def replyActor = Some(Example.reply)
  override def apiKey(auth: Option[UserPass]) : Future[Option[Boolean]] = {
    auth match {
      case Example.auth => Future(Some(true))
      case _ => Future(None)
    }
  }
}

trait ResultService extends HttpService {
  def apiKey(auth: Option[UserPass]) : Future[Option[Boolean]] = {
    auth match {
      case Some(UserPass("hello","hello")) => Future(Some(true))
      case _ => Future(None)
    }
  }
  def user(auth: Option[UserPass]) : Future[Option[String]] = {
    auth match {
      case Some(UserPass(x,y)) => Future(Some(x+ " "+y))
      case _ => Future(None)
    }
  }
  
  def replyActor:Option[akka.actor.ActorRef] = None
  
  def httpBasic[U](realm: String = "Secured Resource",authenticator: UserPassAuthenticator[U]): BasicHttpAuthenticator[U] =
    new BasicHttpAuthenticator[U](realm, authenticator)
  
  implicit val timeout = Timeout(5.seconds)
  val resultRoute = {
    path("search") { get { authenticate(httpBasic[Boolean](authenticator = apiKey)) { _ =>
         parameters('search.as[String]).as(Query) { q => 
           complete { replyActor match {
             case Some(x) => (x ? q).mapTo[JsObject]
             case None => ""
             }
           }
    } } } } ~
    path("auth") { get { authenticate(httpBasic[String](authenticator = user)) { u =>
           complete {u}
    } } } ~
    path("") { get {
      complete {"OK"}
      }
    }
  }
}
