package com.company.model

import net.liftweb.mapper._
import net.liftweb.util.FieldError
import com.company.comet._
import net.liftweb.http.S
import net.liftweb.common.Empty
import com.company.comet.VehicleEvent


class UsedVehicle extends LongKeyedMapper[UsedVehicle] {

  def getSingleton = UsedVehicle

  def primaryKeyField = id
  object id extends MappedLongIndex(this)
  object generatedId extends MappedLong(this)

  object description extends MappedString(this, 100) {
    def checkDescription(desc: String): List[FieldError] = {
      List[FieldError]()
    }
  }

  def printableDescription = "(id: %s, generatedId %s, description %s)".format(
    this.id, this.generatedId, this.description)

//  object title extends MappedString(this, 140) {
//    override def dbIndexed_? = true
//    override def defaultValue = "New Blog Post"
//  }
//  object contents extends MappedText(this)
//  object published extends MappedBoolean(this)
}


object UsedVehicle extends UsedVehicle with LongKeyedMetaMapper[UsedVehicle] {
  override def dbTableName = "used_vehicle"

  val Empty = create.description("No vehicles")
}


object UsedVehicleManager {

  def getUsedVehicles: List[UsedVehicle] = UsedVehicle.findAll match {
    case Nil => UsedVehicle.Empty :: Nil
    case _ @ list => list
  }

  def saveUsedVehicle(description: String, generatedId: Long) {
    val mapper = UsedVehicle.create.description(description).generatedId(generatedId)
    mapper.save()

    // sends a message to master actor, to broadcast the message
    VehicleMaster ! VehicleEvent(UsedVehicleManager.getUsedVehicles)

    val currentActor = S.session.flatMap(_.findComet(classOf[SynchronizerCometActor].getSimpleName, Empty))
    // runs a long task in the background, giving the current actor (browser actor)
    // this way we only send update messages to to browser that did the creation
    SynchronizerActor ! LongTaskEvent(currentActor, 5, mapper.id.get)
  }

  def removeUsedVehicle(id: Long) {
    UsedVehicle.findAll(By(UsedVehicle.id, id)).foreach(_.delete_!)

    // sends a message to master actor
    VehicleMaster ! VehicleEvent(UsedVehicleManager.getUsedVehicles)


  }

}