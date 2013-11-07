package com.company.snippet

import net.liftweb.http.{SHtml, RequestVar}
import com.company.comet.UsedVehicleManager
import com.company.model.UsedVehicle
import net.liftweb.util.Helpers._
import net.liftweb.common.Loggable
import scala.xml.{NodeSeq, Text}
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds._
import net.liftweb.util.CssSel


object Vehicle extends Loggable {

  private object description extends RequestVar("")
  private object generatedId extends RequestVar[Long](0)

  def saveUsedVehicle() {
    UsedVehicleManager.saveUsedVehicle(description.is, generatedId.is)
  }

  def renderVehicles(vehicles: List[UsedVehicle]): CssSel =
      ".entry *" #> vehicles.map(vehicle => {
          val removeFunction: () => JsCmd = {() =>
            UsedVehicleManager.removeUsedVehicle(vehicle.id.get)
            Noop
          }

          ".description *" #> vehicle.description.get &
          ".generatedId *" #> vehicle.generatedId.get &
          ".removeAction *" #> SHtml.ajaxButton(Text("Remove"), removeFunction) &
          ".removeAction [data-vehicle-id]" #> vehicle.id.get.toString
        }
      )

  def render = {
    "#description" #> SHtml.text(description.is, description(_), "" + "maxlength" -> "40", "placeholder" -> "Description") &
    "#generated-id" #> SHtml.text(description.is, s => generatedId(tryo(s.toLong) openOr -1), "placeholder" -> "Generated id") &
    "#submit" #> (SHtml.hidden(saveUsedVehicle) ++ <input type="submit" value="Create a new vehicle"/>) andThen SHtml.makeFormsAjax
  }
}
