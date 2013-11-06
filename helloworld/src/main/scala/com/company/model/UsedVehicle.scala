package com.company.model

import net.liftweb.mapper._
import net.liftweb.mapper.AjaxEditableField
import net.liftweb.util.FieldError


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
