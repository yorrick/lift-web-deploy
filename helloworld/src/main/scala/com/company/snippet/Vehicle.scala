package com.company.snippet

import net.liftweb.http.{SHtml, RequestVar}
import com.company.comet.{VehicleMaster, UsedVehicleManager}
import com.company.model.UsedVehicle
import net.liftweb.util.Helpers._
import com.company.comet.VehicleEvent


class Vehicle {

  private object description extends RequestVar("")
  private object generatedId extends RequestVar[Long](0)

  def saveUsedVehicle() {
    UsedVehicleManager.saveUsedVehicle(description.is, generatedId.is)

    // sends a message to master actor
    VehicleMaster ! VehicleEvent(UsedVehicleManager.getUsedVehicles)
  }

  def getUsedVehiclesStrings: List[String] = UsedVehicle.findAll.map(_.printableDescription)

  def render = {
    "#description" #> SHtml.text(description.is, description(_), "" +
      "maxlength" -> "40", "placeholder" -> "Description") &
      "#generated-id" #> SHtml.text(description.is, s => generatedId(tryo(s.toLong) openOr -1),
        "placeholder" -> "Generated id") &
      "#submit" #> (SHtml.hidden(saveUsedVehicle) ++ <input type="submit" value="Create a new vehicle"/>) andThen SHtml.makeFormsAjax
  }
}
