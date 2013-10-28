package com.company.comet

import net.liftweb._
import http._
import SHtml._
import net.liftweb.common.{Box, Full}
import net.liftweb.util._
import net.liftweb.actor._
import net.liftweb.util.Helpers._
import net.liftweb.http.js.JsCmds.{SetHtml}
import net.liftweb.http.js.JE.Str
import java.util.Date
import scala.xml.Text


class Clock extends CometActor {
  override def defaultPrefix = Full("clk")

  def render = bind("time" -> timeSpan)

  def timeSpan = (<span id="time">{now}</span>)

  // schedule a ping every 10 seconds so we redraw
  Schedule.schedule(this, Tick, 10000L)

  override def lowPriority : PartialFunction[Any, Unit] = {
    case Tick => {
      println("Got tick " + new Date());

      partialUpdate(SetHtml("time", Text(now.toString)))
      // schedule an update in 10 seconds
      Schedule.schedule(this, Tick, 10000L)
    }
  }
}
case object Tick