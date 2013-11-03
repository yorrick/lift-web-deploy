package com.company.snippet

import scala.xml.{Null, NodeSeq, Text, Attribute}
import net.liftweb.util._


class Utils {

  def includeLessFile(linkNode: NodeSeq) = {
      if (Props.getBool("lessProdMode", true)) {
        linkNode
      } else {
        val lessCompiler = <script id="less-compiler" src="/js/external/less.js" type="text/javascript"></script>
        val href = (linkNode \ "@href").toString

        val newLinkNone = href.split('/').toList match {
          case "":: "css" :: remainingPath if remainingPath.last.endsWith(".css") =>
            val path = remainingPath.dropRight(1)
            val cssFileName = remainingPath.last
            val lessFilename = cssFileName.replace(".css", ".less")
            val newPath = "/less/" + path.mkString("/") + lessFilename

            (<link rel="stylesheet/less" type="text/css" media="screen, projection"/>  % Attribute(None, "href", Text(newPath), Null))
              .union(lessCompiler)
          case _ => linkNode
        }

        newLinkNone
      }
  }
}

