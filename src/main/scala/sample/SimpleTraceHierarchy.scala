package sample

import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import scala.util.Random

/**
 * Created by focusj on 14-8-4.
 */
object SimpleTraceHierarchy extends App {

  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val askTimeout: Timeout = 500.milliseconds

  val system = ActorSystem.create("TraceHierarchy", ConfigFactory.load("application"))

  val userAction = system.actorOf(Props[UserAction], "userAction")
  system.scheduler.schedule(3.seconds, 1.second) {
    userAction ! GetUserInfoMsg(s"${System.currentTimeMillis()}${Random.nextString(10)}", "{'1': 'path1', '2': 'path2'}")
  }
  system.awaitTermination()
}
