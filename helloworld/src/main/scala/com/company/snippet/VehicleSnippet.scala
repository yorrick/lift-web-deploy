package com.company.snippet

import net.liftweb.http.{S, SHtml, RequestVar}
import com.company.model.{UsedVehicleManager, UsedVehicle}
import com.company.comet.SynchronizerCometActor
import net.liftweb.util.Helpers._
import net.liftweb.common.{Empty, Loggable}
import scala.xml.{NodeSeq, Text}
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds._
import net.liftweb.util.CssSel


object VehicleSnippet extends Loggable {

  private object description extends RequestVar("")
  private object generatedId extends RequestVar[Long](0)

  def saveUsedVehicle() {
    UsedVehicleManager.saveUsedVehicle(description.is, generatedId.is)
  }

  def render = {
    "#description" #> SHtml.text(description.is, description(_), "" + "maxlength" -> "40", "placeholder" -> "Description") &
    "#generated-id" #> SHtml.text(description.is, s => generatedId(tryo(s.toLong) openOr -1), "placeholder" -> "Generated id") &
    "#submit" #> (SHtml.hidden(saveUsedVehicle) ++ <input type="submit" value="Create"/>) andThen SHtml.makeFormsAjax
  }
}
