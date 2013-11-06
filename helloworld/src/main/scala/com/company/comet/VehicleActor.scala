package com.company.comet

import net.liftweb._
import http._
import SHtml._
import net.liftweb.common.{Loggable, Box, Full}
import net.liftweb.util._
import net.liftweb.util.Helpers._
import net.liftweb.http.js.JsCmds.{Run, Noop}
import scala.xml.{Null, Attribute, Text, NodeSeq}
import scala.actors.Actor
import com.company.model.UsedVehicle
import net.liftweb.mapper.By
import net.liftweb.http.js.JsCmd
import com.company.snippet.Vehicle


case class VehicleEvent(val vehicles: List[UsedVehicle])
case class SubscribeVehicle(actor : VehicleActor)
case class UnsubVehicle(actor : VehicleActor)


object UsedVehicleManager {

  def getUsedVehicles: List[UsedVehicle] = UsedVehicle.findAll match {
    case Nil => UsedVehicle.Empty :: Nil
    case _ @ list => list
  }

  def saveUsedVehicle(description: String, generatedId: Long) {
    val mapper = UsedVehicle.create.description(description).generatedId(generatedId)
    mapper.save()

    // sends a message to master actor
    VehicleMaster ! VehicleEvent(UsedVehicleManager.getUsedVehicles)
  }

  def removeUsedVehicle(id: Long) {
    UsedVehicle.findAll(By(UsedVehicle.id, id)).foreach(_.delete_!)

    // sends a message to master actor
    VehicleMaster ! VehicleEvent(UsedVehicleManager.getUsedVehicles)
  }

}


class VehicleActor extends CometActor {

  def render = "#entries *" #> {html: NodeSeq =>
    println(s"html: $html")
    val result = Vehicle.renderVehicles(UsedVehicleManager.getUsedVehicles)
    println(s"result: $result")
    result
  }

  override def lowPriority : PartialFunction[Any, Unit] = {
    case VehicleEvent(vehicles) => {
      // removes all break line chars since it breaks JS call
      val html = Vehicle.renderVehicles(vehicles).toString.replaceAll("\n", "")
      val js = s"window.App.views.usedVehicles.updateVehiclesTable('$html')"
      partialUpdate(Run(js))
    }
  }

  override def localSetup {
    VehicleMaster ! SubscribeVehicle(this)
    super.localSetup()
  }

  override def localShutdown {
    VehicleMaster ! UnsubVehicle(this)
    super.localShutdown()
  }
}


/**
 * Stores all comet actors
 */
object VehicleMaster extends Actor with Loggable {

  private var vehicleActors : List[VehicleActor] = Nil

  def act = {
    loop {
      react {
        case SubscribeVehicle(va) =>
          logger.info("SubscribeVehicle")
          vehicleActors ::= va
        case UnsubVehicle(va) =>
          logger.info("UnsubVehicle")
          vehicleActors = vehicleActors.filter(_ != va)
        case ve: VehicleEvent =>
          logger.info(s"VehicleEvent $ve")
          vehicleActors.foreach(_ ! ve)
      }
    }
  }

  start()
}