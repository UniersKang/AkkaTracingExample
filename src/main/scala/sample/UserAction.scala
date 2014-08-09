package sample

import java.util.UUID

import akka.actor.Props
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import com.github.levkhomich.akka.tracing.TracingSupport

import scala.concurrent.duration._

case class GetUserInfoMsg(id: String, path: String) extends TracingSupport

case class GetUserInfoRs(info: String, token: String)

case class WrongPathRs(rs: String)

/**
 * Created by focusj on 14-8-6.
 */
class UserAction extends TracableActor {
  private val userService = context.actorOf(Props[UserService], "userService")
  private val pathService = context.actorOf(Props[PathService], "pathService")

  implicit val askTimeout: Timeout = 500.milliseconds

  import context.dispatcher

  def receive = {
    case msg@GetUserInfoMsg(id, path) =>
      println(s"${this.getClass.getSimpleName} receive msg: ${msg.toString}")

      trace.sample(msg, this.getClass.getSimpleName)

      trace.recordKeyValue(msg, "GetUserInfoAction", UUID.randomUUID().toString())

      pathService ? CheckPathMsg(path).asChildOf(msg) map {
        case CheckPathRs(check) if check =>
          userService ? GetVisiableUserInfoMsg(id).asChildOf(msg) zip userService ? GetUserTokenMsg(id).asChildOf(msg) map {
            case (GetVisiableUserInfoRs(rs), GetUserTokenRs(token)) =>
              println(s"Get User Info Result: $rs, And User Token: $token")
              GetUserInfoRs(rs, token).asResponseTo(msg)
          } pipeTo sender()

        case check =>
          WrongPathRs("you input a wrong path").asResponseTo(msg)
      }
  }
}