package com.company 
package snippet 

import scala.xml.{Text, NodeSeq}
import net.liftweb.util._
import net.liftweb.common._
import java.util.Date
import com.company.lib._
import Helpers._
import com.company.model.UsedVehicle
import net.liftweb.http.{RequestVar, S, SHtml}
import net.liftweb.http.js.JsCmds.{SetHtml}
import net.liftweb.http.js.JE.JsRaw
import com.company.comet._
import net.liftweb.http.js.JsCmd
import com.company.comet.VehicleEvent


//object description extends RequestVar("Enter your description here")
//object amount extends RequestVar("0")


//class Admin {
//
//  def onButtonPress(): JsCmd = {
//    println("Going to update clocks for all clients!")
//    ClockMaster ! Tick
//    JsCmd.unitToJsCmd()
//  }
//
//  def updateClocks: NodeSeq => NodeSeq =
//      "#button" #> SHtml.ajaxButton(Text("Update clocks for everybody"), onButtonPress _)
//
//}


object VehicleSnippet {
  private[this] val logger = Logger(getClass().getName());

  private object description extends RequestVar("")
  private object generatedId extends RequestVar[Long](0)

  def saveUsedVehicle() {
    UsedVehicleManager.saveUsedVehicle(description.is, generatedId.is)

    // sends a message to master actor
    VehicleMaster ! VehicleEvent(UsedVehicleManager.getUsedVehicles)
  }

  def getUsedVehiclesStrings: List[String] = UsedVehicle.findAll.map(_.printableDescription)

  def render = {
    println("[VehicleSnippet] enter.")

    "#description" #> SHtml.text(description.is, description(_), "" +
      "maxlength" -> "40", "placeholder" -> "Description") &
    "#generated-id" #> SHtml.text(description.is, s => generatedId(tryo(s.toLong) openOr -1),
      "placeholder" -> "Generated id") &
    "#submit" #> (SHtml.hidden(saveUsedVehicle) ++ <input type="submit" value="Create a new vehicle"/>) andThen SHtml.makeFormsAjax
  }
}


class HelloWorld {
  val logger = Logger(classOf[HelloWorld])

  lazy val date: Box[Date] = DependencyFactory.inject[Date] // inject the date

  // replace the contents of the element with id "time" with the date
  def howdy = "#timeToto *" #> date.map(_.toString)

  def usedVehicles: NodeSeq => NodeSeq = "*" #> getUsedVehiclesStrings
      .map(s => <li>{s}</li>)
      .foldLeft(NodeSeq.Empty)((n1, n2) => n1.union(n2))

  def getUsedVehiclesStrings =
    UsedVehicle.findAll.map(_.printableDescription)
//    UsedVehicle.findAll.map(u => "(id: %s, generatedId %s, description %s)".format(u.id, u.generatedId, u.description))

//  def add (xhtml : NodeSeq) : NodeSeq = {
//    var description = "Enter desc here"
//    var amount = "0"
//
//    def processEntryAdd() = {
//      println("processEntryAdd: " + description + ", " + amount)
//      JsRaw("alert('Added your stuff')")
//    }
//
//    SHtml.ajaxForm(
//      bind("form", xhtml,
//        "description" -> SHtml.text(description, description = _),
//        "amount" -> SHtml.text(amount, amount = _),
//        "submit" -> SHtml.submit("Submit", processEntryAdd _)
//      ) ++ SHtml.hidden(processEntryAdd _)
//    )

//    def onButtonPress() = {
//      logger.info(s"Got an AJAX call with $description with $amount")
//
//      SetHtml("my-div", Text(s"You sent $description with $amount"))
//    }
//
//    bind("form", xhtml,
//      "description" -> SHtml.text(description, description = _),
//      "amount" -> SHtml.text(amount, amount = _),
//      "button" -> SHtml.ajaxButton(Text("Press me"), onButtonPress _)
//    )

//    def processEntryAdd() {
//      println(s"amount $amount, description $description")
//
//      if (amount.is.toDouble <= 0) {
//        S.error("Invalid amount")
//      } else {
//        S.redirectTo("")
//      }
//    }

//    bind("form", xhtml,
//      "description" -> SHtml.text(description.is, description(_)),
//      "amount" -> SHtml.text(amount.is, amount(_)),
//      "submit" -> SHtml.submit("Add", processEntryAdd))
//  }
}

/**
 * Snippet object that configures template-login and connects it to Login.auth
 */
//object Login {
//  private object user extends RequestVar("")
//  private object pass extends RequestVar("")
//
//  def auth() = {
//    val userValue = user.is
//    val passValue = pass.is
//
//    println(s"[Login.auth] $userValue $passValue")
//  }
//
//  def render = {
//    println("[Login.login] enter.")
//
//    "#user" #> SHtml.text(user.is, user(_), "maxlength" -> "40", "placeholder" -> "Username") &
//    "#pass" #> SHtml.password(pass.is, pass(_), "placeholder" -> "Password") &
//    "#submit" #> (SHtml.hidden(auth) ++ <input type="submit" value="Login"/>) andThen SHtml.makeFormsAjax
//  }
//}


