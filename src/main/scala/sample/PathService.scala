package sample

import java.util.UUID

import com.github.levkhomich.akka.tracing.TracingSupport

case class CheckPathMsg(path: String) extends TracingSupport

case class CheckPathRs(rs: Boolean) extends TracingSupport

/**
 * Created by focusj on 14-8-6.
 */
class PathService extends TracableActor {
  def receive = {
    case msg@CheckPathMsg(path) =>
      println(s"${this.getClass.getSimpleName} receive msg: ${msg.toString}")

      trace.sample(msg, this.getClass.getSimpleName)

      trace.recordKeyValue(msg, "CheckPathAction", UUID.randomUUID().toString())

      sender() ! CheckPathRs(true).asResponseTo(msg)
  }
}
