package sample

import java.util.UUID

import akka.actor.{ActorSystem, Props}
import akka.cluster.routing.{ClusterRouterGroup, ClusterRouterGroupSettings}
import akka.routing.{ConsistentHashingGroup, FromConfig}
import com.github.levkhomich.akka.tracing.TracingSupport
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

/**
 * Created by focusj on 14-8-6.
 */
case class FooMsg() extends TracingSupport

case class FooRs()

case class BarMsg() extends TracingSupport

case class BarRs()


class FooServer extends TracableActor {
  def receive = {
    case msg@FooMsg() =>
      println(s"${this.getClass.getSimpleName} receive msg: $msg")

      trace.sample(msg, this.getClass.getSimpleName)
      trace.recordKeyValue(msg, "FooServer", UUID.randomUUID().toString)

      sender() ! FooRs().asResponseTo(msg)
  }
}

class BarServer extends TracableActor {
  var fooActor = context.actorOf(
    ClusterRouterGroup(ConsistentHashingGroup(Nil), ClusterRouterGroupSettings(
      totalInstances = 100, routeesPaths = List("/user/fooRouter"),
      allowLocalRoutees = true, useRole = Some("compute"))).props(),
    name = "fooActor")

  def receive = {
    case msg@BarMsg() =>
      println(s"${this.getClass.getSimpleName} receive msg: $msg")

      trace.sample(msg, this.getClass.getSimpleName)
      trace.recordKeyValue(msg, "FooServer", UUID.randomUUID().toString)

      fooActor ! FooMsg()

  }
}

object FooServer {
  def main(arg: Array[String]) {
    System.setProperty("akka.remote.netty.tcp.port", "2551")

    val system = ActorSystem.create("FooSystem", ConfigFactory.load("cluster"))

    val fooActor = system.actorOf(Props[FooServer].withRouter(FromConfig()), "fooRouter")

    fooActor ! FooMsg()
  }
}

object BarServer {
  import scala.concurrent.ExecutionContext.Implicits.global
  def main(arg: Array[String]) {
    System.setProperty("akka.remote.netty.tcp.port", "2552")

    val system = ActorSystem.create("BarSystem", ConfigFactory.load("cluster"))

    val barActor = system.actorOf(Props[BarServer], "barActor")
    system.scheduler.schedule(3.seconds, 1.second) {
      barActor ! BarMsg()
    }
    system.awaitTermination()

  }
}
