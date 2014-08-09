package sample

import akka.actor.Actor
import com.github.levkhomich.akka.tracing.ActorTracing

/**
 * Created by focusj on 14-8-6.
 */
trait TracableActor extends Actor with ActorTracing