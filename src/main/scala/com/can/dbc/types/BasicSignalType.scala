package com.can.dbc.types

/** Details about Signal endianess, length, name and offset
  *
  * @author Vishrant Gupta
  *
  */
trait BasicSignalType {

  protected var _endianess: String = _

  // length of a signal
  protected var _length: Int = _

  // name of signal
  protected var _name: String = _

  // offset of signal
  protected var _offset: Int = 0

  def endianess: String = if (Option(_endianess).isDefined) _endianess else "little"

  def endianess_=(value: String): Unit = this._endianess = value

  def getLength: Int = if (Option(_length).isDefined) _length else 1

  def length_=(value: Int): Unit = this._length = value

  def name: String = _name

  def name_=(value: String): Unit = this._name = value

  def offset: Int = _offset

  def offset_=(value: Int): Unit = this._offset = value

}
