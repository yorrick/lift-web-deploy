package bootstrap.liftweb

import net.liftweb._
import util._
import Helpers._

import common._
import http._
import js.jquery.JQueryArtifacts
import sitemap._
import Loc._
import mapper._

import com.company.model._
import net.liftmodules.JQueryModule


/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {

  val logger = Logger(classOf[Boot])

  def boot {
    if (!DB.jndiJdbcConnAvailable_?) {
      try {
        val vendor = new StandardDBVendor(
          Props.get("db.driver") openOr "org.postgresql.Driver",
          Props.get("db.url") openOr "jdbc:postgresql:helloworld",
          Props.get("db.user"),
          Props.get("db.password"))

        LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)

        DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)        
        logger.info("Connection to DB is working")
      } catch {
        case t : Throwable =>
          val message = t.toString
          logger.error(s"Could not connect to database: $message")
      }

    }

    // Use Lift's Mapper ORM to populate the database
    // you don't need to use Mapper to use Lift... use
    // any ORM you want
    Schemifier.schemify(true, Schemifier.infoF _, User, UsedVehicle)
    logger.info("Schemify done")

    // where to search snippet
    LiftRules.addToPackages("com.company")

    // Build SiteMap
    def sitemap = SiteMap(
      Menu.i("Home") / "index" >> User.AddUserMenusAfter, // the simple way to declare a menu

      // more complex because this menu allows anything in the
      // /static path to be visible
      Menu(Loc("Static", Link(List("static"), true, "/static/index"), 
	       "Static Content")))

    def sitemapMutators = User.sitemapMutator

    // set the sitemap.  Note if you don't want access control for
    // each page, just comment this line out.
    LiftRules.setSiteMapFunc(() => sitemapMutators(sitemap))

    //Init the jQuery module, see http://liftweb.net/jquery for more information.
    LiftRules.jsArtifacts = JQueryArtifacts
    JQueryModule.InitParam.JQuery=JQueryModule.JQuery172
    JQueryModule.init()

    LiftRules.autoIncludeAjaxCalc.default.set(() => (session: LiftSession) => false)

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)
    
    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // What is the function to test if a user is logged in?
    LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) =>
      new Html5Properties(r.userAgent))

    // Make a transaction span the whole HTTP request
    // TODO install a transaction manager
    S.addAround(DB.buildLoanWrapper)
  }
}
