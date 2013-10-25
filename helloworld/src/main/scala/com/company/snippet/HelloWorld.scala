package com.company 
package snippet 

import scala.xml.{NodeSeq, Text}
import net.liftweb.util._
import net.liftweb.common._
import java.util.Date
import com.company.lib._
import Helpers._
import com.company.model.UsedVehicle

class HelloWorld {
  lazy val date: Box[Date] = DependencyFactory.inject[Date] // inject the date

  // replace the contents of the element with id "time" with the date
  def howdy = "#timeToto *" #> date.map(_.toString)

  def usedVehicles: NodeSeq => NodeSeq = "#vehicles *" #> getUsedVehiclesStrings
      .map(s => <li>{s}</li>)
      .foldLeft(NodeSeq.Empty)((n1, n2) => n1.union(n2))

  def getUsedVehiclesStrings =
    UsedVehicle.findAll.map(u => "(id: %s, generated_id: %s)".format(u.id, u.generated_id))

  /*
   lazy val date: Date = DependencyFactory.time.vend // create the date via factory

   def howdy = "#time *" #> date.toString
   */
}

