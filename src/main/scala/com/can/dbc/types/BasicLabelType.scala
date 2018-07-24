package com.can.dbc.types

/** Defines the label type
  *
  * @author Vishrant Gupta
  *
  */
trait BasicLabelType {

  // label name
  protected var _name: String = _

  // label type
  protected var _type: String = _

  def name: String = _name

  def name_=(value: String): Unit = this._name = value

  def `type`: String = if (Option(_type).isDefined) _type else "value"

  def type_=(value: String): Unit = this._type = value

}
