package com.company.lib

import net.liftweb.http.RequestVar
import scala.collection.mutable
import scala.xml.transform.{RuleTransformer, RewriteRule}
import scala.xml._
import net.liftweb.util.Props


object jsManager {

  object snippets extends RequestVar[mutable.MutableList[JSSnippet]](mutable.MutableList[JSSnippet]())

  trait JSSnippet {

    snippets ++= this :: Nil

    val jsFiles: List[String]
  }

  def addChildren(n: Node, newChildren: NodeSeq) = n match {
    case Elem(prefix, label, attribs, scope, child @ _*) =>
      Elem(prefix, label, attribs, scope, child ++ newChildren : _*)
    case _ => println("Can only add children to elements!"); n
  }

  class AddChildrenTo(label: String, newChildren: NodeSeq) extends RewriteRule {
    override def transform(n: Node) = n match {
      case n @ Elem(_, `label`, _, _, _*) => addChildren(n, newChildren)
      case other => other
    }
  }

  def allJsFiles: List[String] = snippets.is.flatMap(_.jsFiles).toList
  def allJSNodes(jsFiles: List[String]): NodeSeq = {
    val jsFilesToAdd = List("/js/snippets/apps.js") ::: jsFiles
    jsFilesToAdd.map(jsFile => <script type="text/javascript"/> % Attribute(None, "src", Text(jsFile), Null))
  }

  def generatedNewHtml(oldHtml: NodeSeq): NodeSeq = {

    val jsImports = Props.getBool("jsProdMode", true) match {
      case true =>
        <script id="jquery" src="/js/snippets/all-snippets.js" type="text/javascript"></script>
      case false =>
        val jsFiles = allJsFiles
        allJSNodes(jsFiles)
    }

    new RuleTransformer(new AddChildrenTo("head", jsImports)).transform(oldHtml)
  }
}
