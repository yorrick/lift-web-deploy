package com.company.snippet

import scala.xml.NodeSeq
import net.liftweb.util._
import net.liftweb.util.Helpers._


object Utils {

  val EmptyCssCel = "" #> NodeSeq.Empty

  def manageHead = {
    val cssClasses: List[String] = Props.get("removeClasses", "").split(",").toList
    val cssSels: List[CssSel] = cssClasses map {cssClassToRemove: String =>
       cssClassToRemove #> NodeSeq.Empty
    }

    cssSels.foldLeft(EmptyCssCel)((s1: CssSel, s2: CssSel) => s1 and s2)
  }
}

