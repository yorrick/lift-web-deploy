package com.company.comet

import net.liftweb._
import http._
import SHtml._
import net.liftweb.common.{Loggable, Box, Full}
import net.liftweb.util._
import net.liftweb.util.Helpers._
import net.liftweb.http.js.JsCmds.{SetHtml}
import java.util.Date
import scala.xml.Text
import scala.actors.Actor


case object Tick
case class SubscribeClock(clock : Clock)
case class UnsubClock(clock : Clock)


class Clock extends CometActor {
  override def defaultPrefix = Full("clk")

  def render = bind("time" -> timeSpan)

  def timeSpan = <span id="time">{now}</span>

  // schedule a ping every 10 seconds so we redraw
  Schedule.schedule(this, Tick, 10000L)

  override def lowPriority : PartialFunction[Any, Unit] = {
    case Tick => {
      partialUpdate(SetHtml("time", Text(now.toString)))
      // schedule an update in 10 seconds
      Schedule.schedule(this, Tick, 10000L)
    }
  }

  override def localSetup {
    ClockMaster ! SubscribeClock(this)
    super.localSetup()
  }

  override def localShutdown {
    ClockMaster ! UnsubClock(this)
    super.localShutdown()
  }
}


/**
 * Stores all comet actors
 */
object ClockMaster extends Actor with Loggable {

  private var clocks : List[Clock] = Nil

  def act = {
    loop {
      react {
        case SubscribeClock(clk) =>
          logger.info("SubscribeClock")
          clocks ::= clk
        case UnsubClock(clk) =>
          logger.info("UnsubClock")
          clocks = clocks.filter(_ != clk)
        case Tick =>
          logger.info("Tick")
          clocks.foreach(_ ! Tick)
      }
    }
  }

  start()
}