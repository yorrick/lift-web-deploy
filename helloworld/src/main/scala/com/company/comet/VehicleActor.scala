package com.company.comet

import net.liftweb._
import http._
import net.liftweb.http.js.JsCmds._
import scala.xml.{Null, Text, Attribute, NodeSeq}
import com.company.model.{UsedVehicleManager, UsedVehicle}
import com.company.snippet.VehicleSnippet
import net.liftweb._
import actor.LiftActor
import common._
import util._
import Helpers._
import net.liftweb.http.js.JsCmd
import com.company.comet.LongTaskEvent
import net.liftweb.http.js.JsCmds.Replace
import com.company.comet.Unsubscribe
import com.company.comet.Subscribe
import net.liftweb.http.js.JsCmds.Run
import com.company.comet.VehicleEvent


case class LongTaskEvent(val userCometActor: Box[LiftCometActor],
                         val waitFor: Int,
                         val id: Long)

/**
 * Used to send updates of LongTask
 */
class SynchronizerCometActor extends CometActor with Loggable {

  def render = "*" #> ""

  override def lowPriority : PartialFunction[Any, Unit] = {
    case (id: Long, msg) =>
      val statusTdId = "status-" + id
      val elem = <td>{msg}</td> % Attribute(None, "id", Text(statusTdId), Null)

      partialUpdate(Replace(statusTdId, elem))
  }

}


/**
 * Used for long running tasks
 */
object SynchronizerActor extends LiftActor with Loggable {

  protected def messageHandler = {
    case msg @ LongTaskEvent(userCometActor, 0, id) =>
      userCometActor.foreach(_ ! (id, "Done!!!"))
      // removes the done message after a while
      Schedule.schedule(() => userCometActor.foreach(_ ! (id, "")), 5 seconds)

    case msg @ LongTaskEvent(userCometActor, waitFor, id) =>
      userCometActor.foreach(_ ! (id, s"$waitFor second left"))
      val newWaitFor = waitFor - 1
      // simulates a long running task
      Schedule.schedule(() => this ! LongTaskEvent(userCometActor, newWaitFor, id), 1 seconds)
  }
}


case class VehicleEvent(val vehicles: List[UsedVehicle])
case class Subscribe(actor : VehicleActor)
case class Unsubscribe(actor : VehicleActor)


/**
 * Comet actor that just answers to VehicleEvent events and sends html vehicle listing to browser.
 */
class VehicleActor extends CometActor {

  /**
   * Vehicle rendering function
   */
  def renderVehicles(vehicles: List[UsedVehicle]): CssSel =
    ".entry *" #> vehicles.map(vehicle => {
      val removeFunction: () => JsCmd = {() =>
        UsedVehicleManager.removeUsedVehicle(vehicle.id.get)
        Noop
      }

      ".description *" #> vehicle.description.get &
        ".generatedId *" #> vehicle.generatedId.get &
        ".removeAction *" #> SHtml.ajaxButton(Text("Remove"), removeFunction) &
        ".removeAction [data-vehicle-id]" #> vehicle.id.get.toString &
        ".status [id]" #> ("status-" + vehicle.id.get.toString)
    }
  )

  /**
   * Used for initial rendering
   */
  def render = "#entries *" #> renderVehicles(UsedVehicleManager.getUsedVehicles)

  override def lowPriority : PartialFunction[Any, Unit] = {
    case VehicleEvent(vehicles) => {

      // generates HTML by extracting a part of the template, this way
      Templates("index" :: Nil) map {templateContent =>
          val selector: CssSel = ".entry ^^" #> "ignored"
          val templateTableRow: NodeSeq = selector(templateContent)

          // build CssSel
          val usedVehicleCssSel = renderVehicles(vehicles)
          // applies CssSel on html fragment
          val html = usedVehicleCssSel(templateTableRow)
          // removes all break line chars since it breaks JS call
          val noNewLineHtml = html.toString.toString.replaceAll("\n", "")

          // send html fragment to javascript function
          val js = s"window.App.views.usedVehicles.updateVehiclesTable('$noNewLineHtml')"
          partialUpdate(Run(js))
      }

    }
  }

  override def localSetup {
    VehicleMaster ! Subscribe(this)
    super.localSetup()
  }

  override def localShutdown {
    VehicleMaster ! Unsubscribe(this)
    super.localShutdown()
  }
}


/**
 * Stores all comet actors
 */
object VehicleMaster extends LiftActor with Loggable {

  private var vehicleActors : List[VehicleActor] = Nil

  protected def messageHandler = {
    case Subscribe(va) =>
      vehicleActors ::= va
    case Unsubscribe(va) =>
      vehicleActors = vehicleActors.filter(_ != va)
    case ve: VehicleEvent =>
      vehicleActors.foreach(_ ! ve)
  }
}