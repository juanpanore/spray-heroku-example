package com.qmee.sprayherokuexample

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import spray.json._
import spray.routing.authentication._
import HttpHeaders._
import StatusCodes._
import akka.actor.{ActorSystem,Props,Actor}
import akka.testkit.TestActorRef
import akka.pattern.ask
import akka.util.Timeout
import scala.util.Try
import scala.concurrent.ExecutionContext.Implicits.global


class MyServiceSpec extends Specification with Specs2RouteTest with ResultService {
  def actorRefFactory = system
  
  val resultActorRef = system.actorOf(Props[ReplyActor],"reply")
  
  "ResultService" should {

    "return an OK for GET requests to the index path" in {
      Get("/") ~> resultRoute ~> check {
        responseAs[String] must contain("OK")
      }
    }
    
    "return an OK response for GET requests to the search path" in {
      Get("/search?search=hello") ~> Authorization(BasicHttpCredentials("hello", "hello")) ~> resultRoute ~> check {
        status === OK
      }
    }
    "return an Unauthorized response for GET requests to the search path without credentials" in {
      Get("/search?search=hello") ~> resultRoute ~> check {
        handled must beFalse
      }
    }
    "return user password for GET requests to the auth path" in {
      Get("/auth") ~> Authorization(BasicHttpCredentials("hello", "there")) ~> resultRoute ~> check {
        responseAs[String] must contain("hello there")
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/kermit") ~> resultRoute ~> check {
        handled must beFalse
      }
    }

    "return a MethodNotAllowed error for PUT requests to the root path" in {
      Put() ~> sealRoute(resultRoute) ~> check {
        status === MethodNotAllowed
        responseAs[String] === "HTTP method not allowed, supported methods: GET"
      }
    }
  }
}

class MyActorSpec extends Specification with Specs2RouteTest {
  val actorRef = TestActorRef(new ReplyActor)
  implicit val timeout:akka.util.Timeout = akka.util.Timeout(5)
  "Reply Actor" should {
    "return correct response to a query" in {
      val r = (actorRef ? Query("hello")).mapTo[JsObject]
      val correct = JsObject("query" -> JsString("hello"),"answer" -> JsString("hello back !"))
      r.value.mustEqual(Some(scala.util.Success(correct)))
      }
    "return empty response to other message" in {
      val r = (actorRef ? "hello").mapTo[JsObject]
      val correct = JsObject()
      r.value.mustEqual(Some(scala.util.Success(correct)))
      }
  }
}