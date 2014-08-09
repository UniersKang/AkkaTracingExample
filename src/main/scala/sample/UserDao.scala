package sample

import com.github.levkhomich.akka.tracing.TracingSupport


case class GetUserInfoByIdMsg(id: String) extends TracingSupport

case class GetUserInfoByIdRs(id: String)

case class GetTokenByIdMsg(id: String) extends TracingSupport

case class GetTokenByIdRs(rs: String)

/**
 * Created by focusj on 14-8-6.
 */
class UserDao extends TracableActor {
  def receive = {
    case msg@GetUserInfoByIdMsg(uid) =>
      println(s"${this.getClass.getSimpleName} receive msg: ${msg.toString}")

      trace.sample(msg, this.getClass.getSimpleName)

      sender() ! GetUserInfoByIdRs( """{'id': 123456789, 'name': 'focusj', 'gender': 'boy', 'address': '望京街与福安西路交叉口, 望京SOHO, 230710'}""").asResponseTo(msg)

    case msg@GetTokenByIdMsg(id) =>
      println(s"${this.getClass.getSimpleName} receive msg: ${msg.toString}")

      trace.sample(msg, this.getClass.getSimpleName)

      sender() ! GetTokenByIdRs( """{'token': 'CwACAAAAAnNzDAADCAABfwABAQYAAgAACwADAAAAC1VzZX'}""").asResponseTo(msg)
  }
}
