package sample

import java.util.UUID

import akka.actor.Props
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import com.github.levkhomich.akka.tracing.TracingSupport

import scala.concurrent.duration._

case class GetVisiableUserInfoMsg(id: String) extends TracingSupport

case class GetVisiableUserInfoRs(rs: String)

case class GetUserTokenMsg(id: String) extends TracingSupport

case class GetUserTokenRs(rs: String)

/**
 * Created by focusj on 14-8-6.
 */
class UserService extends TracableActor {
  private val userService = context.actorOf(Props[UserDao], "userDao")

  implicit val askTimeout: Timeout = 500.milliseconds

  import context.dispatcher

  def receive = {
    case msg@GetVisiableUserInfoMsg(id) =>
      println(s"${this.getClass.getSimpleName} receive msg: ${msg.toString}")

      trace.sample(msg, this.getClass.getSimpleName)

      trace.recordKeyValue(msg, "GetUserService", UUID.randomUUID().toString())

      userService ? GetUserInfoByIdMsg(id).asChildOf(msg) map {
        case GetUserInfoByIdRs(rs) => GetVisiableUserInfoRs(rs).asResponseTo(msg)
      } pipeTo sender()

    case msg@GetUserTokenMsg(id) =>
      println(s"${this.getClass.getSimpleName} receive msg: ${msg.toString}")

      trace.sample(msg, this.getClass.getSimpleName)

      trace.recordKeyValue(msg, "GetUserService", UUID.randomUUID().toString())

      userService ? GetTokenByIdMsg(id).asChildOf(msg) map {
        case GetTokenByIdRs(rs) => GetTokenByIdRs(rs).asResponseTo(msg)
      } pipeTo sender()
  }
}
