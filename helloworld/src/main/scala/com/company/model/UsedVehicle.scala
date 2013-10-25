package com.company.model

import net.liftweb.mapper._
import net.liftweb.common._


class UsedVehicle extends LongKeyedMapper[UsedVehicle] {

  def getSingleton = UsedVehicle

  def primaryKeyField = id
  object id extends MappedLongIndex(this)
  object generated_id extends MappedLong(this)

//  object title extends MappedString(this, 140) {
//    override def dbIndexed_? = true
//    override def defaultValue = "New Blog Post"
//  }
//  object contents extends MappedText(this)
//  object published extends MappedBoolean(this)
}


object UsedVehicle extends UsedVehicle with LongKeyedMetaMapper[UsedVehicle] {
  override def dbTableName = "used_vehicle"

}
