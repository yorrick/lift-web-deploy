package com.company.snippet

import net.liftweb.http.{SHtml, RequestVar}
import com.company.comet.{VehicleMaster, UsedVehicleManager}
import com.company.model.UsedVehicle
import net.liftweb.util.Helpers._
import com.company.comet.VehicleEvent
import net.liftweb.common.Loggable
import scala.xml.{Null, Attribute, Text, NodeSeq}
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds._


object Vehicle extends Loggable {

  private object description extends RequestVar("")
  private object generatedId extends RequestVar[Long](0)

  def saveUsedVehicle() {
    UsedVehicleManager.saveUsedVehicle(description.is, generatedId.is)
  }

  def renderVehicles(vehicles: List[UsedVehicle]): NodeSeq = {
    val tags = vehicles.map(vehicle => {

      val removeFunction: () => JsCmd = {() =>
        UsedVehicleManager.removeUsedVehicle(vehicle.id.get)
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

  def render = {
    "#description" #> SHtml.text(description.is, description(_), "" +
      "maxlength" -> "40", "placeholder" -> "Description") &
      "#generated-id" #> SHtml.text(description.is, s => generatedId(tryo(s.toLong) openOr -1),
        "placeholder" -> "Generated id") &
      "#submit" #> (SHtml.hidden(saveUsedVehicle) ++ <input type="submit" value="Create a new vehicle"/>) andThen SHtml.makeFormsAjax
  }
}
