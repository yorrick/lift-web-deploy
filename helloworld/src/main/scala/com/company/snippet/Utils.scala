package com.company.snippet

import scala.xml.{Null, NodeSeq, Text, Attribute}
import net.liftweb.util._
import net.liftweb.util.Helpers._


object Utils {

  def manageHead = {
    val classesToRemove = Props.get("cssMode", "dev") match {
      case "dev" => ".prodCss"
      case "prod" => ".devCss"
      case _ => ".devCss"
    }

    classesToRemove #> NodeSeq.Empty
  }
}

