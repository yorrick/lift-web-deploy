package com.company.comet

import net.liftweb._
import http._
import SHtml._
import net.liftweb.common.{Box, Full}
import net.liftweb.util._
import net.liftweb.util.Helpers._
import net.liftweb.http.js.JsCmds.{Run, Noop}
import scala.xml.{Null, Attribute, Text, NodeSeq}
import scala.actors.Actor
import com.company.model.UsedVehicle
import net.liftweb.mapper.By
import net.liftweb.http.js.JsCmd


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
  override def defaultPrefix = Full("vehicle")

  def render = bind("entries" -> renderVehicles(UsedVehicleManager.getUsedVehicles))

  def renderVehicles(vehicles: List[UsedVehicle]): NodeSeq = {
    val tags = vehicles.map(vehicle => {

      val removeFunction: () => JsCmd = {() =>
        UsedVehicleManager.removeUsedVehicle(vehicle.id)
        Noop
      }

      val removeButton = SHtml.ajaxButton(Text("Remove"), removeFunction)

      <tr>
        <td>{vehicle.description}</td>
        <td>{vehicle.generatedId}</td>
        <td>{removeButton}</td>
      </tr> % Attribute(None, "data-vehicle-id", Text(vehicle.id.toString), Null)
    }
    ).foldLeft(NodeSeq.Empty)((n1, n2) => n1.union(n2))

    <table id="entries">{tags}</table>
  }

  override def lowPriority : PartialFunction[Any, Unit] = {
    case VehicleEvent(vehicles) => {
      // removes all break line chars since it breaks JS call
      val html = renderVehicles(vehicles).toString.replaceAll("\n", "")
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
object VehicleMaster extends Actor {

  private var vehicleActors : List[VehicleActor] = Nil

  def act = {
    loop {
      react {
        case SubscribeVehicle(va) =>
          println("SubscribeVehicle")
          vehicleActors ::= va
        case UnsubVehicle(va) =>
          println("UnsubVehicle")
          vehicleActors = vehicleActors.filter(_ != va)
        case ve: VehicleEvent =>
          println(s"VehicleEvent $ve")
          vehicleActors.foreach(_ ! ve)
      }
    }
  }

  start()
}