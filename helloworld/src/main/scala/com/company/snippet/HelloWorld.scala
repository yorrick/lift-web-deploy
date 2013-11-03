package com.company 
package snippet 

import scala.xml._
import net.liftweb.util._
import net.liftweb.common._
import java.util.Date
import com.company.lib._
import Helpers._
import com.company.model.UsedVehicle
import net.liftweb.http.{RequestVar, SHtml}
import com.company.comet._
import com.company.comet.VehicleEvent


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
}
